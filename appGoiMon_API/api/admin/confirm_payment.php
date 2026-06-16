<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $sessionId = int_param($params, 'session_id');
    ensure_positive($sessionId, 'Thiếu session_id');

    $stmt = $pdo->prepare("SELECT * FROM table_sessions WHERE id = ? AND status = 'pending_payment'");
    $stmt->execute([$sessionId]);
    $session = $stmt->fetch();
    if (!$session) {
        json_response(false, 'Phiên không tồn tại hoặc không chờ thanh toán', null, 409);
    }

    $pdo->beginTransaction();
    $pdo->prepare("UPDATE table_sessions SET payment_status = 'paid', status = 'active' WHERE id = ?")->execute([$sessionId]);
    $pdo->prepare("UPDATE restaurant_tables SET status = 'occupied' WHERE id = ?")->execute([(int) $session['table_id']]);
    $pdo->commit();

    json_response(true, 'Đã xác nhận thanh toán và mở bàn', ['session_id' => $sessionId, 'status' => 'active']);
});
