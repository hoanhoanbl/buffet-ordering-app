<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $sessionId = int_param($params, 'session_id');
    ensure_positive($sessionId, 'Thiếu session_id');

    $stmt = $pdo->prepare("SELECT * FROM table_sessions WHERE id = ? AND status = 'active'");
    $stmt->execute([$sessionId]);
    $session = $stmt->fetch();
    if (!$session) {
        json_response(false, 'Phiên bàn không hoạt động hoặc đã yêu cầu thanh toán', null, 409);
    }

    $pdo->beginTransaction();
    $pdo->prepare("UPDATE table_sessions SET status = 'checkout_requested' WHERE id = ?")->execute([$sessionId]);
    $pdo->prepare("UPDATE tables SET status = 'checkout_requested' WHERE id = ?")->execute([(int) $session['table_id']]);
    $pdo->commit();

    json_response(true, 'Đã gửi yêu cầu thanh toán', ['session_id' => $sessionId, 'status' => 'checkout_requested']);
});
