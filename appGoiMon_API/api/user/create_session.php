<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $comboId = int_param($params, 'combo_id');
    $paidGuests = int_param($params, 'paid_guest_count');
    $freeChildren = max(0, int_param($params, 'free_child_count'));
    $paymentMethod = str_param($params, 'payment_method', 'cash');

    ensure_positive($comboId, 'Thieu combo_id');
    ensure_positive($paidGuests, 'So khach tinh tien phai lon hon 0');

    if (!in_array($paymentMethod, ['cash', 'qr'], true)) {
        json_response(false, 'Phuong thuc thanh toan khong hop le', null, 422);
    }

    $table = fetch_table($pdo, $params);
    if (!$table) {
        json_response(false, 'Sai ma ban', null, 404);
    }
    release_expired_sessions($pdo, (int) $table['id']);
    $table = fetch_table($pdo, $params) ?: $table;

    if (($table['status'] ?? '') !== 'available') {
        json_response(false, 'Ban dang duoc su dung', null, 409);
    }

    $comboStmt = $pdo->prepare("SELECT * FROM buffet_combos WHERE id = ? AND status = 'active'");
    $comboStmt->execute([$comboId]);
    $combo = $comboStmt->fetch();
    if (!$combo) {
        json_response(false, 'Combo khong ton tai hoac da ngung hoat dong', null, 404);
    }

    $activeStmt = $pdo->prepare('SELECT id FROM table_sessions WHERE table_id = ? AND ' . active_session_condition() . ' LIMIT 1');
    $activeStmt->execute([(int) $table['id']]);
    if ($activeStmt->fetch()) {
        json_response(false, 'Ban dang co phien hoat dong', null, 409);
    }

    $totalAmount = $paidGuests * (float) $combo['price_per_person'];

    $pdo->beginTransaction();
    $insert = $pdo->prepare(
        "INSERT INTO table_sessions
         (table_id, combo_id, paid_guest_count, free_child_count, payment_method, payment_status, paid_at, status, total_amount, start_time, end_time)
         VALUES (?, ?, ?, ?, ?, 'paid', NOW(), 'active', ?, NOW(), DATE_ADD(NOW(), INTERVAL 100 MINUTE))"
    );
    $insert->execute([(int) $table['id'], $comboId, $paidGuests, $freeChildren, $paymentMethod, $totalAmount]);
    $sessionId = (int) $pdo->lastInsertId();

    $pdo->prepare("UPDATE restaurant_tables SET status = 'occupied' WHERE id = ?")->execute([(int) $table['id']]);
    $pdo->commit();

    $sessionStmt = $pdo->prepare(
        'SELECT ts.*, t.table_code, t.table_name, c.combo_name AS combo_name
         FROM table_sessions ts
         JOIN restaurant_tables t ON t.id = ts.table_id
         JOIN buffet_combos c ON c.id = ts.combo_id
         WHERE ts.id = ?'
    );
    $sessionStmt->execute([$sessionId]);
    $session = decorate_session($pdo, $sessionStmt->fetch() ?: []);

    json_response(true, 'Da thanh toan va mo phien buffet', [
        'session_id' => $sessionId,
        'table_id' => (int) $table['id'],
        'combo' => $combo,
        'total_amount' => number_format($totalAmount, 2, '.', ''),
        'status' => 'active',
        'session' => $session,
    ], 201);
});
