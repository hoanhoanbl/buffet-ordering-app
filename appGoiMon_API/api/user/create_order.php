<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $sessionId = int_param($params, 'session_id');
    $note = str_param($params, 'note');
    $items = $params['items'] ?? [];

    ensure_positive($sessionId, 'Thieu session_id');
    if (!is_array($items) || count($items) === 0) {
        json_response(false, 'Danh sach mon khong duoc rong', null, 422);
    }

    $sessionStmt = $pdo->prepare("SELECT * FROM table_sessions WHERE id = ?");
    $sessionStmt->execute([$sessionId]);
    $session = $sessionStmt->fetch();
    if (!$session) {
        json_response(false, 'Khong tim thay phien ban', null, 404);
    }

    $session = decorate_session($pdo, $session);
    if (($session['status'] ?? '') !== 'active' || ($session['payment_status'] ?? '') !== 'paid') {
        json_response(false, 'Phien ban khong hoat dong hoac chua thanh toan', null, 409);
    }

    if (!empty($session['is_expired'])) {
        json_response(false, 'Da het thoi gian dung bua, khong the goi them mon', null, 409);
    }

    $pdo->beginTransaction();
    $orderCode = 'ORD' . date('YmdHis') . random_int(100, 999);
    $orderStmt = $pdo->prepare("INSERT INTO orders (session_id, order_code, status, note) VALUES (?, ?, 'pending', ?)");
    $orderStmt->execute([$sessionId, $orderCode, $note]);
    $orderId = (int) $pdo->lastInsertId();

    $foodCheck = $pdo->prepare(
        "SELECT f.id
         FROM combo_menu_items cf
         JOIN menu_items f ON f.id = cf.menu_item_id
         WHERE cf.combo_id = ? AND f.id = ? AND f.status = 'available'"
    );
    $itemStmt = $pdo->prepare(
        "INSERT INTO order_items (order_id, menu_item_id, quantity, note, status)
         VALUES (?, ?, ?, ?, 'pending')"
    );

    $createdItems = [];
    foreach ($items as $item) {
        if (!is_array($item)) {
            continue;
        }
        $foodId = int_param($item, 'food_id');
        $quantity = int_param($item, 'quantity');
        $itemNote = str_param($item, 'note');

        if ($foodId <= 0 || $quantity <= 0) {
            $pdo->rollBack();
            json_response(false, 'Moi mon can co food_id va quantity lon hon 0', null, 422);
        }

        $foodCheck->execute([(int) $session['combo_id'], $foodId]);
        if (!$foodCheck->fetch()) {
            $pdo->rollBack();
            json_response(false, "Mon {$foodId} khong thuoc combo hoac khong kha dung", null, 422);
        }

        $itemStmt->execute([$orderId, $foodId, $quantity, $itemNote]);
        $createdItems[] = [
            'order_item_id' => (int) $pdo->lastInsertId(),
            'food_id' => $foodId,
            'quantity' => $quantity,
            'status' => 'pending',
        ];
    }

    if (count($createdItems) === 0) {
        $pdo->rollBack();
        json_response(false, 'Danh sach mon khong hop le', null, 422);
    }

    $pdo->commit();
    json_response(true, 'Gui don goi mon thanh cong', [
        'order_id' => $orderId,
        'items' => $createdItems,
    ], 201);
});
