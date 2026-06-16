<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $pdo = db();
    $params = $_GET;
    $sessionId = int_param($params, 'session_id');

    if ($sessionId > 0) {
        $stmt = $pdo->prepare(
            'SELECT ts.*, t.table_code, t.table_name, c.combo_name AS combo_name
             FROM table_sessions ts
             JOIN restaurant_tables t ON t.id = ts.table_id
             JOIN buffet_combos c ON c.id = ts.combo_id
             WHERE ts.id = ?'
        );
        $stmt->execute([$sessionId]);
    } else {
        $table = fetch_table($pdo, $params);
        if (!$table) {
            json_response(false, 'Thieu session_id hoac ma ban khong hop le', null, 422);
        }
        $stmt = $pdo->prepare(
            'SELECT ts.*, t.table_code, t.table_name, c.combo_name AS combo_name
             FROM table_sessions ts
             JOIN restaurant_tables t ON t.id = ts.table_id
             JOIN buffet_combos c ON c.id = ts.combo_id
             WHERE ts.table_id = ? AND ' . active_session_condition('ts') . '
             ORDER BY ts.id DESC LIMIT 1'
        );
        $stmt->execute([(int) $table['id']]);
    }

    $session = $stmt->fetch();
    if (!$session) {
        json_response(false, 'Khong tim thay phien ban', null, 404);
    }

    json_response(true, 'Thanh cong', decorate_session($pdo, $session));
});
