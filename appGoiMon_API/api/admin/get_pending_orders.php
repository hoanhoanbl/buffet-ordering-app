<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $status = str_param($_GET, 'status', 'pending');
    $allowed = ['pending', 'approved', 'served', 'rejected'];
    if (!in_array($status, $allowed, true)) {
        json_response(false, 'Trang thai mon khong hop le', null, 422);
    }

    $stmt = db()->prepare(
        "SELECT oi.id AS order_item_id, oi.order_id, o.session_id, ts.table_id,
                t.table_code, t.table_name, oi.menu_item_id AS food_id, f.item_name AS food_name,
                f.image,
                oi.quantity, oi.note, oi.status, oi.created_at
         FROM order_items oi
         JOIN orders o ON o.id = oi.order_id
         JOIN table_sessions ts ON ts.id = o.session_id
         JOIN restaurant_tables t ON t.id = ts.table_id
         JOIN menu_items f ON f.id = oi.menu_item_id
         WHERE oi.status = ?
         ORDER BY oi.created_at ASC, oi.id ASC"
    );
    $stmt->execute([$status]);

    json_response(true, 'Thanh cong', $stmt->fetchAll());
});
