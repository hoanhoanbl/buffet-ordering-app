<?php

declare(strict_types=1);

function refresh_order_status(PDO $pdo, int $orderId): void
{
    $stmt = $pdo->prepare(
        "SELECT
            SUM(status = 'pending') AS pending_count,
            SUM(status = 'processing') AS processing_count,
            SUM(status = 'served') AS served_count,
            SUM(status = 'rejected') AS rejected_count,
            COUNT(*) AS total_count
         FROM order_items
         WHERE order_id = ?"
    );
    $stmt->execute([$orderId]);
    $row = $stmt->fetch() ?: [];

    $status = 'pending';
    if ((int) ($row['total_count'] ?? 0) === 0) {
        $status = 'empty';
    } elseif ((int) ($row['pending_count'] ?? 0) > 0) {
        $status = 'pending';
    } elseif ((int) ($row['processing_count'] ?? 0) > 0) {
        $status = 'processing';
    } elseif ((int) ($row['served_count'] ?? 0) > 0 && (int) ($row['rejected_count'] ?? 0) === 0) {
        $status = 'served';
    } elseif ((int) ($row['served_count'] ?? 0) > 0) {
        $status = 'partially_served';
    } else {
        $status = 'rejected';
    }

    $pdo->prepare('UPDATE orders SET status = ? WHERE id = ?')->execute([$status, $orderId]);
}

function update_order_item_status(string $targetStatus, string $message): void
{
    $pdo = db();
    $params = input();
    $orderItemId = int_param($params, 'order_item_id');
    ensure_positive($orderItemId, 'Thiếu order_item_id');

    $stmt = $pdo->prepare('SELECT id, order_id, status FROM order_items WHERE id = ?');
    $stmt->execute([$orderItemId]);
    $item = $stmt->fetch();
    if (!$item) {
        json_response(false, 'Không tìm thấy món trong đơn', null, 404);
    }

    $pdo->beginTransaction();
    $pdo->prepare('UPDATE order_items SET status = ? WHERE id = ?')->execute([$targetStatus, $orderItemId]);
    refresh_order_status($pdo, (int) $item['order_id']);
    $pdo->commit();

    json_response(true, $message, [
        'order_item_id' => $orderItemId,
        'status' => $targetStatus,
    ]);
}
