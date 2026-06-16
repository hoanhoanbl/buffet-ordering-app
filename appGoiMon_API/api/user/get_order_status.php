<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $sessionId = int_param($_GET, 'session_id');
    ensure_positive($sessionId, 'Thiếu session_id');

    $stmt = db()->prepare(
        'SELECT o.id AS order_id, o.status AS order_status, o.note AS order_note, o.created_at AS order_created_at,
                oi.id AS order_item_id, oi.menu_item_id AS food_id, f.item_name AS food_name, f.image, oi.quantity,
                oi.note AS item_note, oi.status AS item_status, oi.created_at, oi.updated_at
         FROM orders o
         JOIN order_items oi ON oi.order_id = o.id
         JOIN menu_items f ON f.id = oi.menu_item_id
         WHERE o.session_id = ?
         ORDER BY o.id DESC, oi.id ASC'
    );
    $stmt->execute([$sessionId]);

    json_response(true, 'Thành công', $stmt->fetchAll());
});
