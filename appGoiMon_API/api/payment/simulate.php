<?php

// SIMULATION ONLY — stands in for the external payment gateway; delete/disable in production.
// The customer app must NOT be trusted to set paid status directly. Tapping the demo button here
// merely asks this mock gateway to "report" a payment; the server then validates the amount and
// flips the status via the same confirm_session_payment() used by the real webhook.
//
// On localhost there is no publicly reachable URL for a real gateway to POST back to, so we call
// confirm_session_payment() directly (approach a). A real gateway would instead build the
// HMAC-signed payload and POST it to webhook.php — sketched in the commented block below.

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();

    // Resolve the target session: explicit session_id, or the active unpaid session for a table.
    $sessionId = int_param($params, 'session_id');
    $session = null;

    if ($sessionId > 0) {
        $stmt = $pdo->prepare("SELECT id, total_amount, payment_status FROM table_sessions WHERE id = ? AND status = 'active'");
        $stmt->execute([$sessionId]);
        $session = $stmt->fetch();
    } else {
        $table = fetch_table($pdo, $params);
        if (!$table) {
            json_response(false, 'Thieu session_id hoac ma ban khong hop le', null, 422);
        }
        $stmt = $pdo->prepare(
            "SELECT id, total_amount, payment_status FROM table_sessions
             WHERE table_id = ? AND status = 'active'
             ORDER BY id DESC LIMIT 1"
        );
        $stmt->execute([(int) $table['id']]);
        $session = $stmt->fetch();
    }

    if (!$session) {
        json_response(false, 'Khong tim thay phien dang hoat dong', null, 404);
    }

    $sessionId = (int) $session['id'];
    $amount = (float) $session['total_amount'];
    $transactionId = 'SIM' . time() . random_int(1000, 9999);

    // Approach (a): apply directly — simplest for localhost (no reachable callback URL needed).
    $result = confirm_session_payment($pdo, $sessionId, $amount, $transactionId, 'qr');

    // Approach (b) — what a real gateway does: sign the payload and POST it to webhook.php.
    // $payload = json_encode(['order_code' => "BUFFET{$sessionId}", 'amount' => $amount,
    //     'transaction_id' => $transactionId, 'method' => 'qr'], JSON_UNESCAPED_UNICODE);
    // $signature = hash_hmac('sha256', $payload, PAYMENT_WEBHOOK_SECRET);
    // ... then HTTP POST $payload to api/payment/webhook.php with header X-Signature: $signature.

    switch ($result['result']) {
        case 'not_found':
            json_response(false, 'Phien khong ton tai', $result, 404);
            break;
        case 'amount_mismatch':
            json_response(false, 'So tien thanh toan khong khop', $result, 422);
            break;
        default:
            json_response(true, 'Cong gia lap da xac nhan thanh toan', $result, 200);
    }
});
