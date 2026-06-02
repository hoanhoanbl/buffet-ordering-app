<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $comboId = int_param($params, 'combo_id');
    $paidGuests = int_param($params, 'paid_guest_count');
    $freeChildren = int_param($params, 'free_child_count');
    $paymentMethod = str_param($params, 'payment_method', 'cash');

    ensure_positive($comboId, 'Thiếu combo_id');
    ensure_positive($paidGuests, 'Số khách trả phí phải lớn hơn 0');

    $table = fetch_table($pdo, $params);
    if (!$table) {
        json_response(false, 'Bàn không tồn tại', null, 404);
    }

    $comboStmt = $pdo->prepare("SELECT * FROM buffet_combos WHERE id = ? AND status = 'active'");
    $comboStmt->execute([$comboId]);
    $combo = $comboStmt->fetch();
    if (!$combo) {
        json_response(false, 'Combo không tồn tại hoặc đã ngừng hoạt động', null, 404);
    }

    $activeStmt = $pdo->prepare('SELECT id FROM table_sessions WHERE table_id = ? AND ' . active_session_condition() . ' LIMIT 1');
    $activeStmt->execute([(int) $table['id']]);
    if ($activeStmt->fetch()) {
        json_response(false, 'Bàn đang có phiên hoạt động', null, 409);
    }

    $totalAmount = $paidGuests * (int) $combo['price_per_person'];

    $pdo->beginTransaction();
    $insert = $pdo->prepare(
        "INSERT INTO table_sessions
         (table_id, combo_id, paid_guest_count, free_child_count, payment_method, payment_status, status, total_amount)
         VALUES (?, ?, ?, ?, ?, 'unpaid', 'pending_payment', ?)"
    );
    $insert->execute([(int) $table['id'], $comboId, $paidGuests, $freeChildren, $paymentMethod, $totalAmount]);
    $sessionId = (int) $pdo->lastInsertId();

    $updateTable = $pdo->prepare("UPDATE tables SET status = 'pending_payment' WHERE id = ?");
    $updateTable->execute([(int) $table['id']]);
    $pdo->commit();

    json_response(true, 'Tạo phiên thành công, vui lòng chờ admin xác nhận thanh toán', [
        'session_id' => $sessionId,
        'table_id' => (int) $table['id'],
        'combo' => $combo,
        'total_amount' => $totalAmount,
        'status' => 'pending_payment',
    ], 201);
});
