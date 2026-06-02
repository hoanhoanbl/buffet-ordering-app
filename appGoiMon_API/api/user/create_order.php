<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $sessionId = int_param($params, 'session_id');
    $note = str_param($params, 'note');
    $items = $params['items'] ?? [];

    ensure_positive($sessionId, 'Thiếu session_id');
    if (!is_array($items) || count($items) === 0) {
        json_response(false, 'Danh sách món không được rỗng', null, 422);
    }

    $sessionStmt = $pdo->prepare("SELECT * FROM table_sessions WHERE id = ? AND status = 'active'");
    $sessionStmt->execute([$sessionId]);
    $session = $sessionStmt->fetch();
    if (!$session) {
        json_response(false, 'Phiên bàn chưa được xác nhận hoặc đã kết thúc', null, 409);
    }

    $pdo->beginTransaction();
    $orderStmt = $pdo->prepare("INSERT INTO orders (session_id, status, note) VALUES (?, 'pending', ?)");
    $orderStmt->execute([$sessionId, $note]);
    $orderId = (int) $pdo->lastInsertId();

    $foodCheck = $pdo->prepare(
        "SELECT f.id
         FROM combo_foods cf
         JOIN foods f ON f.id = cf.food_id
         WHERE cf.combo_id = ? AND f.id = ? AND f.status = 'available'"
    );
    $itemStmt = $pdo->prepare(
        "INSERT INTO order_items (order_id, food_id, quantity, note, status)
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
            json_response(false, 'Mỗi món cần có food_id và quantity lớn hơn 0', null, 422);
        }

        $foodCheck->execute([(int) $session['combo_id'], $foodId]);
        if (!$foodCheck->fetch()) {
            $pdo->rollBack();
            json_response(false, "Món {$foodId} không thuộc combo hoặc không khả dụng", null, 422);
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
        json_response(false, 'Danh sách món không hợp lệ', null, 422);
    }

    $pdo->commit();
    json_response(true, 'Gửi đơn gọi món thành công', [
        'order_id' => $orderId,
        'items' => $createdItems,
    ], 201);
});
