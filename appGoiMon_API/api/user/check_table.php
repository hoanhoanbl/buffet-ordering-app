<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $table = fetch_table($pdo, $params);

    if (!$table) {
        json_response(false, 'Sai ma ban', null, 404);
    }
    release_expired_sessions($pdo, (int) $table['id']);
    $table = fetch_table($pdo, $params) ?: $table;

    $stmt = $pdo->prepare(
        'SELECT ts.*, t.table_code, t.table_name, c.combo_name AS combo_name
         FROM table_sessions ts
         JOIN restaurant_tables t ON t.id = ts.table_id
         JOIN buffet_combos c ON c.id = ts.combo_id
         WHERE ts.table_id = ? AND ' . active_session_condition('ts') . '
         ORDER BY ts.id DESC LIMIT 1'
    );
    $stmt->execute([(int) $table['id']]);
    $session = $stmt->fetch();

    if ($session) {
        $session = decorate_session($pdo, $session);
    }

    json_response(true, 'Thanh cong', [
        'table' => $table,
        'session' => $session ?: null,
    ]);
});
