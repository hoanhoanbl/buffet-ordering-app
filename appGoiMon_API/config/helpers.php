<?php

declare(strict_types=1);

require_once __DIR__ . '/database.php';

date_default_timezone_set('Asia/Ho_Chi_Minh');

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
