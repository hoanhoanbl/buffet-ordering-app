<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $sessionId = int_param($_GET, 'session_id');
    ensure_positive($sessionId, 'Thieu session_id');

    $stmt = db()->prepare(
        'SELECT
            o.id AS order_id,
            o.created_at,
            oi.id AS order_item_id,
            f.item_name AS food_name,
            f.image,
            oi.quantity,
            oi.note,
            oi.status
         FROM orders o
         JOIN order_items oi ON oi.order_id = o.id
         JOIN menu_items f ON f.id = oi.menu_item_id
         WHERE o.session_id = ?
         ORDER BY o.created_at DESC, oi.id ASC'
    );
    $stmt->execute([$sessionId]);

    $orders = [];
    foreach ($stmt->fetchAll() as $row) {
        $orderId = (int) $row['order_id'];
        if (!isset($orders[$orderId])) {
            $orders[$orderId] = [
                'order_id' => $orderId,
                'created_at' => $row['created_at'],
                'items' => [],
            ];
        }

        $orders[$orderId]['items'][] = [
            'food_name' => $row['food_name'],
            'image' => $row['image'],
            'quantity' => (int) $row['quantity'],
            'note' => $row['note'],
            'status' => $row['status'],
        ];
    }

    json_response(true, 'Thanh cong', array_values($orders));
});
