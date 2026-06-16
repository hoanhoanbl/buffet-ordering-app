<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $pdo = db();
    $params = $_GET;
    $sessionId = int_param($params, 'session_id');

    if ($sessionId > 0) {
        $sessionStmt = $pdo->prepare(
            'SELECT ts.*, t.table_code, t.table_name, bc.combo_name AS combo_name, bc.price_per_person
             FROM table_sessions ts
             JOIN restaurant_tables t ON t.id = ts.table_id
             JOIN buffet_combos bc ON bc.id = ts.combo_id
             WHERE ts.id = ?'
        );
        $sessionStmt->execute([$sessionId]);
    } else {
        $table = fetch_table($pdo, $params);
        if (!$table) {
            json_response(false, 'Thieu session_id hoac thong tin ban khong hop le', null, 422);
        }
        $sessionStmt = $pdo->prepare(
            'SELECT ts.*, t.table_code, t.table_name, bc.combo_name AS combo_name, bc.price_per_person
             FROM table_sessions ts
             JOIN restaurant_tables t ON t.id = ts.table_id
             JOIN buffet_combos bc ON bc.id = ts.combo_id
             WHERE ts.table_id = ? AND ' . active_session_condition('ts') . '
             ORDER BY ts.id DESC LIMIT 1'
        );
        $sessionStmt->execute([(int) $table['id']]);
    }

    $session = $sessionStmt->fetch();
    if (!$session) {
        json_response(false, 'Khong tim thay phien ban', null, 404);
    }
    $session = decorate_session($pdo, $session);

    $itemsStmt = $pdo->prepare(
        'SELECT o.id AS order_id, o.status AS order_status, o.created_at AS order_created_at,
                oi.id AS order_item_id, oi.menu_item_id AS food_id, f.item_name AS food_name, oi.quantity,
                oi.note, oi.status AS item_status, oi.created_at, oi.updated_at
         FROM orders o
         JOIN order_items oi ON oi.order_id = o.id
         JOIN menu_items f ON f.id = oi.menu_item_id
         WHERE o.session_id = ?
         ORDER BY o.id DESC, oi.id ASC'
    );
    $itemsStmt->execute([(int) $session['id']]);
    $items = $itemsStmt->fetchAll();

    $unfinishedCount = 0;
    foreach ($items as $item) {
        if (in_array($item['item_status'], ['pending', 'approved'], true)) {
            $unfinishedCount++;
        }
    }

    json_response(true, 'Thanh cong', [
        'session' => $session,
        'order_items' => $items,
        'unfinished_item_count' => $unfinishedCount,
        'has_unfinished_items' => $unfinishedCount > 0,
    ]);
});
