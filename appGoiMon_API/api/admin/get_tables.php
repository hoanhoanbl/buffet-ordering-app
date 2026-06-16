<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $pdo = db();
    release_expired_sessions($pdo);
    $stmt = $pdo->query(
        'SELECT t.id, t.table_code, t.table_name, t.status,
                ts.id AS session_id, ts.combo_id, bc.combo_name AS combo_name,
                ts.paid_guest_count, ts.free_child_count, ts.payment_method,
                ts.payment_status, ts.status AS session_status, ts.total_amount,
                ts.start_time, ts.end_time, ts.paid_at
         FROM restaurant_tables t
         LEFT JOIN table_sessions ts ON ts.id = (
             SELECT id FROM table_sessions
             WHERE table_id = t.id AND status = \'active\'
             ORDER BY id DESC LIMIT 1
         )
         LEFT JOIN buffet_combos bc ON bc.id = ts.combo_id
         ORDER BY t.id ASC'
    );

    $tables = array_map(
        function (array $table) use ($pdo): array {
            if (!empty($table['session_id'])) {
                $session = decorate_session($pdo, [
                    'id' => $table['session_id'],
                    'status' => $table['session_status'],
                    'start_time' => $table['start_time'],
                    'end_time' => $table['end_time'],
                ]);
                $table['session_status'] = $session['status'];
                $table['end_time'] = $session['end_time'];
                $table['is_expired'] = $session['is_expired'];
                $table['remaining_seconds'] = $session['remaining_seconds'];
                $table['remaining_minutes'] = $session['remaining_minutes'];
            }
            return $table;
        },
        $stmt->fetchAll()
    );

    json_response(true, 'Thanh cong', $tables);
});
