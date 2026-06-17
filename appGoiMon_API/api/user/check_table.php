<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    // Current logged-in user. Used to enforce session ownership: a user may only resume their OWN
    // active session. 0 / missing => anonymous (can never own an existing session).
    $userId = int_param($params, 'user_id');
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
        $sessionOwner = isset($session['user_id']) ? (int) $session['user_id'] : 0;
        // Only the owner may resume. A session owned by someone else (or a legacy NULL-owner
        // session vs. an identified user) blocks the table — do NOT leak it as resumable.
        if ($sessionOwner <= 0 || $userId <= 0 || $sessionOwner !== $userId) {
            json_response(false, 'Bàn đang được khách khác sử dụng', null, 409);
        }
        $session = decorate_session($pdo, $session);
    }

    json_response(true, 'Thanh cong', [
        'table' => $table,
        'session' => $session ?: null,
    ]);
});
