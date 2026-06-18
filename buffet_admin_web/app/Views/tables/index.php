<?php
/** @var array $tables */
$tables = $tables ?? [];
$selectedTable = $selectedTable ?? null;
$tableItems = $tableItems ?? [];

$statusLabels = ['available' => 'Trống', 'occupied' => 'Đang sử dụng'];
$statusClass = ['available' => 'ready', 'occupied' => 'busy'];

// Số phút còn lại của phiên (nếu end_time ở tương lai).
$remainingMins = static function (?string $endTime): ?int {
    if (empty($endTime)) {
        return null;
    }
    $diff = strtotime($endTime) - time();
    return $diff > 0 ? (int)ceil($diff / 60) : 0;
};
?>
<section class="module-panel">
    <form class="filter-bar" method="get">
        <div class="input-group">
            <span class="input-group-text"><i class="fa-solid fa-magnifying-glass"></i></span>
            <input class="form-control" name="search" value="<?= e($search ?? '') ?>" placeholder="Tìm kiếm bàn...">
        </div>
        <select class="form-select" name="status">
            <option value="">Tất cả trạng thái</option>
            <?php foreach ($statusLabels as $key => $label): ?>
                <option value="<?= $key ?>" <?= ($status ?? '') === $key ? 'selected' : '' ?>><?= $label ?></option>
            <?php endforeach; ?>
        </select>
        <button class="btn btn-primary" type="submit">Lọc</button>
    </form>

    <div class="table-workspace">
        <div class="table-status-grid large">
            <?php foreach ($tables as $table): ?>
                <?php
                $cls = $statusClass[$table['status']] ?? 'ready';
                $isSelected = (int)($selectedTable['id'] ?? 0) === (int)$table['id'];
                $rem = $remainingMins($table['end_time'] ?? null);
                ?>
                <a class="table-chip <?= $cls ?> <?= $isSelected ? 'selected' : '' ?>" href="tables.php?id=<?= (int)$table['id'] ?>">
                    <strong><?= e($table['name']) ?></strong>
                    <span><?= e($statusLabels[$table['status']] ?? $table['status']) ?></span>
                    <?php if (!empty($table['combo_name'])): ?>
                        <small><i class="fa-solid fa-layer-group"></i> <?= e($table['combo_name']) ?></small>
                    <?php endif; ?>
                    <?php if (!empty($table['session_id'])): ?>
                        <small><?= money($table['total_amount'] ?? 0) ?><?= $rem !== null ? ' · ' . ($rem > 0 ? $rem . ' phút' : 'Hết giờ') : '' ?></small>
                    <?php endif; ?>
                </a>
            <?php endforeach; ?>
            <?php if (!$tables): ?>
                <div class="empty-state">Không tìm thấy bàn phù hợp.</div>
            <?php endif; ?>
        </div>

        <aside class="detail-panel">
            <?php if ($selectedTable): ?>
                <?php
                $hasSession = !empty($selectedTable['session_id']);
                $unfinished = (int)($selectedTable['unfinished_count'] ?? 0);
                $rem = $remainingMins($selectedTable['end_time'] ?? null);
                $payMethodLabel = ['qr' => 'Chuyển khoản / QR', 'cash' => 'Tiền mặt'][$selectedTable['payment_method'] ?? ''] ?? '—';
                ?>
                <div class="panel-heading">
                    <div>
                        <h2><?= e($selectedTable['name']) ?></h2>
                        <p><?= e($selectedTable['table_code'] ?? '') ?> · <?= e($statusLabels[$selectedTable['status']] ?? $selectedTable['status']) ?></p>
                    </div>
                    <span class="badge text-bg-dark">#<?= (int)$selectedTable['id'] ?></span>
                </div>

                <?php if ($hasSession): ?>
                    <div class="session-info">
                        <div><span>Combo</span><strong><?= e($selectedTable['combo_name'] ?? '—') ?></strong></div>
                        <div><span>Tạm tính</span><strong><?= money($selectedTable['total_amount'] ?? 0) ?></strong></div>
                        <div><span>Khách trả tiền</span><strong><?= (int)($selectedTable['paid_guest_count'] ?? 0) ?></strong></div>
                        <div><span>Trẻ miễn phí</span><strong><?= (int)($selectedTable['free_child_count'] ?? 0) ?></strong></div>
                        <div><span>Thanh toán</span><strong><?= ($selectedTable['payment_status'] ?? '') === 'paid' ? 'Đã thanh toán' : 'Chưa thanh toán' ?></strong></div>
                        <div><span>Hình thức</span><strong><?= e($payMethodLabel) ?></strong></div>
                        <?php if ($rem !== null): ?>
                            <div><span>Thời gian còn lại</span><strong><?= $rem > 0 ? $rem . ' phút' : 'Hết giờ' ?></strong></div>
                        <?php endif; ?>
                    </div>
                <?php else: ?>
                    <div class="empty-state">Bàn đang trống, chưa có phiên buffet.</div>
                <?php endif; ?>

                <h3>Món đã gọi</h3>
                <div class="ordered-list">
                    <?php foreach ($tableItems as $item): ?>
                        <div class="ordered-row">
                            <strong><?= e($item['menu_name']) ?></strong>
                            <span>x<?= (int)$item['quantity'] ?></span>
                            <span class="badge status-<?= e($item['status']) ?>"><?= e(order_status_label($item['status'])) ?></span>
                        </div>
                    <?php endforeach; ?>
                    <?php if (!$tableItems): ?>
                        <p class="text-muted mb-0">Bàn chưa có món.</p>
                    <?php endif; ?>
                </div>

                <?php if ($hasSession): ?>
                    <div class="detail-actions">
                        <?php if ($unfinished > 0): ?>
                            <p class="close-hint"><i class="fa-solid fa-triangle-exclamation"></i> Còn <?= $unfinished ?> món chưa phục vụ/từ chối — không thể đóng bàn.</p>
                        <?php endif; ?>
                        <form method="post">
                            <input type="hidden" name="id" value="<?= (int)$selectedTable['id'] ?>">
                            <input type="hidden" name="action" value="pay">
                            <button class="btn btn-success w-100" type="submit">Xác nhận thanh toán & trả bàn</button>
                        </form>
                        <form method="post">
                            <input type="hidden" name="id" value="<?= (int)$selectedTable['id'] ?>">
                            <input type="hidden" name="action" value="close">
                            <button class="btn btn-outline-danger w-100" type="submit" <?= $unfinished > 0 ? 'disabled' : '' ?>>Đóng bàn</button>
                        </form>
                    </div>
                <?php endif; ?>
            <?php else: ?>
                <div class="empty-state">Chọn một bàn để xem chi tiết phiên, món đã gọi và thanh toán.</div>
            <?php endif; ?>
        </aside>
    </div>
</section>
