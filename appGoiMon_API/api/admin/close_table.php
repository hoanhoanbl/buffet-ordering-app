<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $sessionId = int_param($params, 'session_id');
    ensure_positive($sessionId, 'Thiếu session_id');

    $stmt = $pdo->prepare("SELECT * FROM table_sessions WHERE id = ? AND status IN ('active', 'checkout_requested')");
    $stmt->execute([$sessionId]);
    $session = $stmt->fetch();
    if (!$session) {
        json_response(false, 'Phiên không tồn tại hoặc đã đóng', null, 409);
    }

    $pdo->beginTransaction();
    $pdo->prepare("UPDATE table_sessions SET status = 'closed', end_time = NOW() WHERE id = ?")->execute([$sessionId]);
    $pdo->prepare("UPDATE tables SET status = 'available' WHERE id = ?")->execute([(int) $session['table_id']]);
    $pdo->commit();

    json_response(true, 'Đã đóng phiên và trả bàn về trống', ['session_id' => $sessionId, 'status' => 'closed']);
});
