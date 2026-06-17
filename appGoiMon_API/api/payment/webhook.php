<?php

// PRODUCTION ENDPOINT — payment gateway callback receiver.
// In production, point the real gateway (VNPAY/payOS/SePay) here; this is the ONLY endpoint that
// should flip payment status. It trusts the request solely because the body carries a valid
// HMAC-SHA256 signature computed with the shared PAYMENT_WEBHOOK_SECRET.

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();

    $rawBody = file_get_contents('php://input') ?: '';

    // Timing-safe signature verification. The gateway sends the HMAC of the exact raw body.
    $provided = $_SERVER['HTTP_X_SIGNATURE'] ?? '';
    $expected = hash_hmac('sha256', $rawBody, PAYMENT_WEBHOOK_SECRET);

    if ($provided === '' || !hash_equals($expected, $provided)) {
        json_response(false, 'Chu ky khong hop le', null, 401);
    }

    $payload = json_decode($rawBody, true);
    if (!is_array($payload)) {
        json_response(false, 'Du lieu khong hop le', null, 422);
    }

    // The gateway echoes our order code / memo. We embed the session id as BUFFET<session_id>.
    $orderCode = str_param($payload, 'order_code', str_param($payload, 'memo'));
    $sessionId = int_param($payload, 'session_id');
    if ($sessionId <= 0 && preg_match('/BUFFET(\d+)/i', $orderCode, $m)) {
        $sessionId = (int) $m[1];
    }
    ensure_positive($sessionId, 'Thieu session_id');

    $amount = (float) ($payload['amount'] ?? 0);
    $transactionId = str_param($payload, 'transaction_id');
    if ($transactionId === '') {
        json_response(false, 'Thieu transaction_id', null, 422);
    }
    $method = str_param($payload, 'method', 'qr');

    $result = confirm_session_payment($pdo, $sessionId, $amount, $transactionId, $method);

    switch ($result['result']) {
        case 'not_found':
            json_response(false, 'Phien khong ton tai', $result, 404);
            break;
        case 'amount_mismatch':
            json_response(false, 'So tien thanh toan khong khop', $result, 422);
            break;
        default:
            // 'ok' and 'duplicate' are both successful from the gateway's perspective.
            json_response(true, 'Da xac nhan thanh toan', $result, 200);
    }
});
