<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $status = str_param($_GET, 'status', 'pending');
    $allowed = ['pending', 'approved', 'served', 'rejected'];
    if (!in_array($status, $allowed, true)) {
        json_response(false, 'Trạng thái món không hợp lệ', null, 422);
    }

    // Optional date filter (YYYY-MM-DD). Keeps the order list scoped to one day instead of
    // dumping the full history into a single screen.
    $date = str_param($_GET, 'date');
    $where = 'oi.status = ?';
    $bind = [$status];
    if ($date !== '') {
        if (preg_match('/^\d{4}-\d{2}-\d{2}$/', $date) !== 1) {
            json_response(false, 'Ngày lọc không hợp lệ', null, 422);
        }
        $where .= ' AND DATE(oi.created_at) = ?';
        $bind[] = $date;
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
         WHERE {$where}
         ORDER BY oi.created_at ASC, oi.id ASC"
    );
    $stmt->execute($bind);

    json_response(true, 'Thành công', $stmt->fetchAll());
});
