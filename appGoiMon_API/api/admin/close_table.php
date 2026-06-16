<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $sessionId = int_param($params, 'session_id');
    ensure_positive($sessionId, 'Thieu session_id');

    $stmt = $pdo->prepare("SELECT * FROM table_sessions WHERE id = ? AND status IN ('active', 'expired')");
    $stmt->execute([$sessionId]);
    $session = $stmt->fetch();
    if (!$session) {
        json_response(false, 'Phien khong ton tai hoac da dong', null, 409);
    }
    $session = decorate_session($pdo, $session);

    $unfinishedStmt = $pdo->prepare(
        "SELECT COUNT(*) AS total
         FROM order_items oi
         JOIN orders o ON o.id = oi.order_id
         WHERE o.session_id = ? AND oi.status IN ('pending', 'approved')"
    );
    $unfinishedStmt->execute([$sessionId]);
    $unfinishedCount = (int) (($unfinishedStmt->fetch() ?: [])['total'] ?? 0);

    $pdo->beginTransaction();
    $pdo->prepare("UPDATE table_sessions SET status = 'closed' WHERE id = ?")->execute([$sessionId]);
    $pdo->prepare("UPDATE restaurant_tables SET status = 'available' WHERE id = ?")->execute([(int) $session['table_id']]);
    $pdo->commit();

    json_response(true, 'Da dong phien va tra ban ve trong', [
        'session_id' => $sessionId,
        'status' => 'closed',
        'unfinished_item_count' => $unfinishedCount,
        'had_unfinished_items' => $unfinishedCount > 0,
    ]);
});
