<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $stmt = db()->query(
        "SELECT oi.id AS order_item_id, oi.order_id, o.session_id, ts.table_id,
                t.table_code, t.table_name, oi.food_id, f.name AS food_name,
                oi.quantity, oi.note, oi.status, oi.created_at
         FROM order_items oi
         JOIN orders o ON o.id = oi.order_id
         JOIN table_sessions ts ON ts.id = o.session_id
         JOIN tables t ON t.id = ts.table_id
         JOIN foods f ON f.id = oi.food_id
         WHERE oi.status = 'pending'
         ORDER BY oi.created_at ASC, oi.id ASC"
    );

    json_response(true, 'Thành công', $stmt->fetchAll());
});
