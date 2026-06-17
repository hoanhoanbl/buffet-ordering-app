<?php

declare(strict_types=1);

require_once __DIR__ . '/database.php';

date_default_timezone_set('Asia/Ho_Chi_Minh');

// Shared secret used to sign/verify payment-gateway webhook callbacks (HMAC-SHA256).
// In production this MUST be overridden via the PAYMENT_WEBHOOK_SECRET environment variable
// and kept identical to the value configured in the real gateway dashboard.
if (!defined('PAYMENT_WEBHOOK_SECRET')) {
    define('PAYMENT_WEBHOOK_SECRET', getenv('PAYMENT_WEBHOOK_SECRET') ?: 'buffet_local_dev_webhook_secret');
}

/*
 * ---------------------------------------------------------------------------
 * MERCHANT BANK DETAILS for the REAL VietQR (Napas EMVCo) bank-transfer QR.
 *
 * Fill these with YOUR bank details. The QR is generated OFFLINE (we build the
 * EMV payload string ourselves and the app renders it). No VietQR/bank API key
 * and no business license are needed. Money is transferred DIRECTLY to this
 * account by whoever scans the QR with their banking app.
 *
 * Each value is env-overridable (set the matching environment variable to use
 * a different account without editing this file).
 *
 * MERCHANT_BANK_BIN: 6-digit Napas BIN of your bank. Common BINs:
 *   Vietcombank 970436, VietinBank 970415, BIDV 970418, Techcombank 970407,
 *   MB Bank 970422, ACB 970416, VPBank 970432, Sacombank 970403,
 *   TPBank 970423, MSB 970426
 * MERCHANT_ACCOUNT_NO:   your bank account number (digits only).
 * MERCHANT_ACCOUNT_NAME: account holder name, UPPERCASE with NO diacritics.
 * ---------------------------------------------------------------------------
 */
if (!defined('MERCHANT_BANK_BIN')) {
    // PLACEHOLDER — 970422 is MB Bank. Replace with your bank's BIN.
    define('MERCHANT_BANK_BIN', getenv('MERCHANT_BANK_BIN') ?: '970422');
}
if (!defined('MERCHANT_ACCOUNT_NO')) {
    // PLACEHOLDER — replace with your real account number.
    define('MERCHANT_ACCOUNT_NO', getenv('MERCHANT_ACCOUNT_NO') ?: 'CHANGE_ME_ACCOUNT_NUMBER');
}
if (!defined('MERCHANT_ACCOUNT_NAME')) {
    // PLACEHOLDER — replace with the account holder name (UPPERCASE, no diacritics).
    define('MERCHANT_ACCOUNT_NAME', getenv('MERCHANT_ACCOUNT_NAME') ?: 'NGUYEN VAN A');
}

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

function json_response(bool $success, string $message, mixed $data = null, int $statusCode = 200): void
{
    http_response_code($statusCode);
    echo json_encode([
        'success' => $success,
        'message' => $message,
        'data' => $data,
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

function require_method(string $method): void
{
    if ($_SERVER['REQUEST_METHOD'] !== strtoupper($method)) {
        json_response(false, 'Phương thức không hợp lệ', null, 405);
    }
}

function input(): array
{
    $raw = file_get_contents('php://input');
    $json = json_decode($raw ?: '', true);

    if (is_array($json)) {
        return $json;
    }

    return $_POST ?: $_GET ?: [];
}

function int_param(array $source, string $key, int $default = 0): int
{
    return isset($source[$key]) ? (int) $source[$key] : $default;
}

function str_param(array $source, string $key, string $default = ''): string
{
    return isset($source[$key]) ? trim((string) $source[$key]) : $default;
}

function active_session_condition(string $alias = ''): string
{
    $column = $alias === '' ? 'status' : "{$alias}.status";
    return "{$column} = 'active'";
}

function release_expired_sessions(PDO $pdo, ?int $tableId = null): void
{
    $where = $tableId === null ? '' : ' AND table_id = ?';
    $params = $tableId === null ? [] : [$tableId];

    $expire = $pdo->prepare(
        "UPDATE table_sessions
         SET status = 'expired'
         WHERE status = 'active'
           AND COALESCE(end_time, DATE_ADD(COALESCE(start_time, NOW()), INTERVAL 100 MINUTE)) <= NOW()
           {$where}"
    );
    $expire->execute($params);

    $release = $pdo->prepare(
        "UPDATE restaurant_tables t
         SET t.status = 'available'
         WHERE t.status = 'occupied'
           AND NOT EXISTS (
               SELECT 1
               FROM table_sessions ts
               WHERE ts.table_id = t.id
                 AND ts.status = 'active'
           )" . ($tableId === null ? '' : ' AND t.id = ?')
    );
    $release->execute($params);
}

function calculate_session_timing(array $session): array
{
    $now = new DateTimeImmutable('now');
    $start = empty($session['start_time'])
        ? $now
        : new DateTimeImmutable((string) $session['start_time']);
    $end = empty($session['end_time'])
        ? $start->modify('+100 minutes')
        : new DateTimeImmutable((string) $session['end_time']);

    $remainingSeconds = max(0, $end->getTimestamp() - $now->getTimestamp());
    $isExpired = $remainingSeconds <= 0 || ($session['status'] ?? '') === 'expired';

    $session['end_time'] = $end->format('Y-m-d H:i:s');
    $session['is_expired'] = $isExpired;
    $session['remaining_seconds'] = $remainingSeconds;
    $session['remaining_minutes'] = (int) ceil($remainingSeconds / 60);

    return $session;
}

function refresh_session_expiration(PDO $pdo, array $session): array
{
    $session = calculate_session_timing($session);

    if (($session['status'] ?? '') === 'active' && $session['is_expired']) {
        $session['status'] = 'expired';
        $stmt = $pdo->prepare("UPDATE table_sessions SET status = 'expired' WHERE id = ? AND status = 'active'");
        $stmt->execute([(int) $session['id']]);
        if (!empty($session['table_id'])) {
            $pdo->prepare("UPDATE restaurant_tables SET status = 'available' WHERE id = ?")->execute([(int) $session['table_id']]);
        }
    }

    return $session;
}

function decorate_session(PDO $pdo, array $session): array
{
    return refresh_session_expiration($pdo, $session);
}

function fetch_table(PDO $pdo, array $params): ?array
{
    $tableId = int_param($params, 'table_id');
    $tableCode = str_param($params, 'table_code');

    if ($tableId > 0) {
        $stmt = $pdo->prepare('SELECT id, table_code, table_name, status FROM restaurant_tables WHERE id = ?');
        $stmt->execute([$tableId]);
        $table = $stmt->fetch();
        return $table ?: null;
    }

    if ($tableCode !== '') {
        $stmt = $pdo->prepare('SELECT id, table_code, table_name, status FROM restaurant_tables WHERE table_code = ?');
        $stmt->execute([$tableCode]);
        $table = $stmt->fetch();
        return $table ?: null;
    }

    return null;
}

function ensure_positive(int $value, string $message): void
{
    if ($value <= 0) {
        json_response(false, $message, null, 422);
    }
}

function public_user(array $user): array
{
    unset($user['password']);
    return $user;
}

function find_user_by_username(PDO $pdo, string $username): ?array
{
    $stmt = $pdo->prepare(
        'SELECT id, username, password, full_name, phone, role, created_at
         FROM users
         WHERE username = ?
         LIMIT 1'
    );
    $stmt->execute([$username]);
    $user = $stmt->fetch();

    return $user ?: null;
}

function password_matches(string $plainPassword, string $storedPassword): bool
{
    return password_verify($plainPassword, $storedPassword) || $plainPassword === $storedPassword;
}

/**
 * Encode one EMVCo TLV field: 2-char ID + 2-char zero-padded length + value.
 * E.g. emv_field('00', '01') => "000201".
 */
function emv_field(string $id, string $value): string
{
    return $id . str_pad((string) strlen($value), 2, '0', STR_PAD_LEFT) . $value;
}

/**
 * CRC-16/CCITT-FALSE (poly 0x1021, init 0xFFFF, no final XOR) over $data,
 * returned as 4 UPPERCASE hex chars. Used for EMVCo VietQR field 63.
 *
 * Self-check: crc16_ccitt_false('123456789') === '29B1'.
 */
function crc16_ccitt_false(string $data): string
{
    $crc = 0xFFFF;
    $len = strlen($data);
    for ($i = 0; $i < $len; $i++) {
        $crc ^= (ord($data[$i]) << 8);
        for ($b = 0; $b < 8; $b++) {
            if (($crc & 0x8000) !== 0) {
                $crc = (($crc << 1) ^ 0x1021) & 0xFFFF;
            } else {
                $crc = ($crc << 1) & 0xFFFF;
            }
        }
    }
    return strtoupper(str_pad(dechex($crc), 4, '0', STR_PAD_LEFT));
}

/**
 * Build a DYNAMIC VietQR (Napas EMVCo) bank-transfer payload string OFFLINE.
 * The returned string is a genuine, scannable QR content: a banking app reading
 * it pre-fills a transfer to {bin}/{accountNo} for {amount} VND with {memo}.
 *
 * Field layout:
 *   00 Payload Format Indicator   = "01"
 *   01 Point of Initiation Method = "12" (dynamic / one-time)
 *   38 Merchant Account Info (nested):
 *        00 GUID            = "A000000727"
 *        01 (nested): 00 = {bin}, 01 = {accountNo}
 *        02 Service code    = "QRIBFTTA" (transfer to account)
 *   52 Merchant Category Code     = "0000"
 *   53 Transaction Currency       = "704" (VND)
 *   54 Transaction Amount         = {amount, integer string}  (omitted if <= 0)
 *   58 Country Code               = "VN"
 *   62 Additional Data (nested): 08 = {memo} (purpose / addInfo)
 *   63 CRC = CRC-16/CCITT-FALSE over the whole string incl. "6304".
 */
function build_vietqr_payload(string $bin, string $accountNo, float $amount, string $memo, string $accountName = ''): string
{
    $beneficiary = emv_field('00', $bin) . emv_field('01', $accountNo);
    $merchantAccount =
        emv_field('00', 'A000000727') .
        emv_field('01', $beneficiary) .
        emv_field('02', 'QRIBFTTA');

    $payload =
        emv_field('00', '01') .
        emv_field('01', '12') .
        emv_field('38', $merchantAccount) .
        emv_field('52', '0000') .
        emv_field('53', '704');

    if ($amount > 0) {
        $payload .= emv_field('54', (string) (int) round($amount));
    }

    $payload .= emv_field('58', 'VN');

    if ($memo !== '') {
        $additionalData = emv_field('08', $memo);
        $payload .= emv_field('62', $additionalData);
    }

    // CRC is computed over the whole payload INCLUDING the "6304" prefix.
    $payload .= '63' . '04';
    return $payload . crc16_ccitt_false($payload);
}

/**
 * Convenience wrapper: build the VietQR payload + the human-readable bank fields
 * for a session, using the configured merchant account and "BUFFET<id>" memo.
 * Returns an associative array ready to merge into an API response.
 */
function vietqr_payment_fields(int $sessionId, float $amount): array
{
    $memo = 'BUFFET' . $sessionId;
    return [
        'vietqr_payload' => build_vietqr_payload(
            MERCHANT_BANK_BIN,
            MERCHANT_ACCOUNT_NO,
            $amount,
            $memo,
            MERCHANT_ACCOUNT_NAME
        ),
        'bank_account_no' => MERCHANT_ACCOUNT_NO,
        'bank_account_name' => MERCHANT_ACCOUNT_NAME,
        'bank_name_or_bin' => MERCHANT_BANK_BIN,
    ];
}

/**
 * Server-side, idempotent payment confirmation. This is the ONLY place that flips a session to
 * 'paid'. It is called from the gateway webhook (production) and from the local simulator (dev).
 *
 * Flow:
 *  - Record the transaction in the `payments` ledger via INSERT IGNORE. A duplicate transaction_id
 *    (gateway retry, double tap) yields affected_rows == 0 and returns a 'duplicate' result so the
 *    payment is never applied twice.
 *  - Load the session and verify the paid amount covers the session total (reject underpayment).
 *  - Flip the session to payment_status='paid', status='active' only if it is not already paid.
 *
 * Returns an array shaped: ['result' => 'ok'|'duplicate'|'amount_mismatch'|'not_found', ...].
 */
function confirm_session_payment(
    PDO $pdo,
    int $sessionId,
    float $amount,
    string $transactionId,
    string $method
): array {
    $ledger = $pdo->prepare(
        'INSERT IGNORE INTO payments (session_id, transaction_id, amount, method, status, created_at)
         VALUES (?, ?, ?, ?, \'paid\', NOW())'
    );
    $ledger->execute([$sessionId, $transactionId, $amount, $method]);

    if ($ledger->rowCount() === 0) {
        // transaction_id already processed — idempotent no-op.
        return ['result' => 'duplicate', 'session_id' => $sessionId, 'transaction_id' => $transactionId];
    }

    $stmt = $pdo->prepare('SELECT id, table_id, total_amount, payment_status FROM table_sessions WHERE id = ?');
    $stmt->execute([$sessionId]);
    $session = $stmt->fetch();

    if (!$session) {
        return ['result' => 'not_found', 'session_id' => $sessionId];
    }

    $total = (float) $session['total_amount'];
    if ($amount + 0.001 < $total) {
        return [
            'result' => 'amount_mismatch',
            'session_id' => $sessionId,
            'paid_amount' => $amount,
            'required_amount' => $total,
        ];
    }

    $update = $pdo->prepare(
        "UPDATE table_sessions
         SET payment_status = 'paid', paid_at = COALESCE(paid_at, NOW()), status = 'active'
         WHERE id = ? AND payment_status <> 'paid'"
    );
    $update->execute([$sessionId]);

    if (!empty($session['table_id'])) {
        $pdo->prepare("UPDATE restaurant_tables SET status = 'occupied' WHERE id = ?")
            ->execute([(int) $session['table_id']]);
    }

    return [
        'result' => 'ok',
        'session_id' => $sessionId,
        'transaction_id' => $transactionId,
        'amount' => $amount,
    ];
}

function run_endpoint(callable $handler): void
{
    try {
        $handler();
    } catch (PDOException $e) {
        json_response(false, 'Lỗi cơ sở dữ liệu', ['error' => $e->getMessage()], 500);
    } catch (Throwable $e) {
        json_response(false, 'Lỗi hệ thống', ['error' => $e->getMessage()], 500);
    }
}
