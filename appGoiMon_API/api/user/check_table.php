<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $table = fetch_table($pdo, $params);

    if (!$table) {
        json_response(false, 'Bàn không tồn tại', null, 404);
    }

    $stmt = $pdo->prepare(
        'SELECT id, combo_id, paid_guest_count, free_child_count, payment_status, status, total_amount, start_time
         FROM table_sessions
         WHERE table_id = ? AND ' . active_session_condition() . '
         ORDER BY id DESC LIMIT 1'
    );
    $stmt->execute([(int) $table['id']]);

    json_response(true, 'Thành công', [
        'table' => $table,
        'session' => $stmt->fetch() ?: null,
    ]);
});
