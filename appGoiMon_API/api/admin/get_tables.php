<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $stmt = db()->query(
        'SELECT t.id, t.table_code, t.table_name, t.status,
                ts.id AS session_id, ts.combo_id, bc.name AS combo_name,
                ts.paid_guest_count, ts.free_child_count, ts.payment_method,
                ts.payment_status, ts.status AS session_status, ts.total_amount, ts.start_time
         FROM tables t
         LEFT JOIN table_sessions ts ON ts.id = (
             SELECT id FROM table_sessions
             WHERE table_id = t.id AND status IN (\'pending_payment\', \'active\', \'checkout_requested\')
             ORDER BY id DESC LIMIT 1
         )
         LEFT JOIN buffet_combos bc ON bc.id = ts.combo_id
         ORDER BY t.id ASC'
    );

    json_response(true, 'Thành công', $stmt->fetchAll());
});
