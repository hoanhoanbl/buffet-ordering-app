<?php
/** @var array $items */
$items = $items ?? [];
$counts = $counts ?? ['pending' => 0, 'approved' => 0, 'served' => 0, 'rejected' => 0];
$statusFilter = $statusFilter ?? 'pending';
$dateFilter = $dateFilter ?? '';

$tabs = [
    'pending' => 'Chờ duyệt',
    'approved' => 'Đã duyệt',
    'served' => 'Đã phục vụ',
    'rejected' => 'Từ chối',
];

// Gom món theo đơn (order_id).
$groups = [];
foreach ($items as $it) {
    $groups[$it['order_id']]['head'] = $it;
    $groups[$it['order_id']]['items'][] = $it;
}

$qs = static function (string $status) use ($dateFilter): string {
    $p = ['status' => $status];
    if ($dateFilter !== '') {
        $p['date'] = $dateFilter;
    }
    return 'orders.php?' . http_build_query($p);
};
?>
<section class="module-panel">
    <div class="orders-toolbar">
        <div class="status-tabs">
            <?php foreach ($tabs as $key => $label): ?>
                <a class="status-tab <?= $statusFilter === $key ? 'active' : '' ?> tab-<?= $key ?>" href="<?= e($qs($key)) ?>">
                    <?= $label ?> <span class="tab-count"><?= (int)($counts[$key] ?? 0) ?></span>
                </a>
            <?php endforeach; ?>
        </div>
        <form class="date-filter" method="get">
            <input type="hidden" name="status" value="<?= e($statusFilter) ?>">
            <input class="form-control form-control-sm" type="date" name="date" value="<?= e($dateFilter) ?>">
            <button class="btn btn-sm btn-primary" type="submit">Lọc ngày</button>
            <?php if ($dateFilter !== ''): ?>
                <a class="btn btn-sm btn-outline-secondary" href="orders.php?status=<?= e($statusFilter) ?>">Bỏ lọc</a>
            <?php endif; ?>
        </form>
    </div>
</section>

<?php if (!$groups): ?>
    <section class="module-panel mt-3">
        <div class="empty-state">Không có món nào ở trạng thái "<?= e($tabs[$statusFilter] ?? $statusFilter) ?>".</div>
    </section>
<?php else: ?>
    <div class="order-group-list mt-3">
        <?php foreach ($groups as $orderId => $group): ?>
            <?php $head = $group['head']; ?>
            <article class="order-card status-edge-<?= e($head['status']) ?>">
                <header class="order-card-head">
                    <div>
                        <strong><?= e($head['table_name']) ?></strong>
                        <small><?= e($head['table_code'] ?? '') ?> · Đơn <?= e($head['order_code'] ?? ('#' . $orderId)) ?></small>
                    </div>
                    <span class="badge status-<?= e($head['status']) ?>"><?= e(order_status_label($head['status'])) ?></span>
                </header>
                <div class="order-item-rows">
                    <?php foreach ($group['items'] as $item): ?>
                        <?php $img = img_url($item['image'] ?? ''); ?>
                        <div class="order-item-row">
                            <?php if ($img !== ''): ?>
                                <img class="food-thumb" src="<?= e($img) ?>" alt="">
                            <?php else: ?>
                                <span class="food-thumb placeholder"><i class="fa-solid fa-image"></i></span>
                            <?php endif; ?>
                            <div class="oi-info">
                                <strong><?= e($item['menu_name']) ?></strong>
                                <?php if (!empty($item['note'])): ?>
                                    <small class="oi-note"><i class="fa-solid fa-pen"></i> <?= e($item['note']) ?></small>
                                <?php endif; ?>
                            </div>
                            <span class="oi-qty">x<?= (int)$item['quantity'] ?></span>
                            <div class="row-actions">
                                <?php if ($item['status'] === 'pending'): ?>
                                    <form method="post">
                                        <input type="hidden" name="id" value="<?= (int)$item['id'] ?>">
                                        <input type="hidden" name="filter_status" value="<?= e($statusFilter) ?>">
                                        <input type="hidden" name="filter_date" value="<?= e($dateFilter) ?>">
                                        <button name="action" value="approve" class="btn btn-sm btn-success" type="submit">Duyệt</button>
                                    </form>
                                    <form method="post">
                                        <input type="hidden" name="id" value="<?= (int)$item['id'] ?>">
                                        <input type="hidden" name="filter_status" value="<?= e($statusFilter) ?>">
                                        <input type="hidden" name="filter_date" value="<?= e($dateFilter) ?>">
                                        <button name="action" value="reject" class="btn btn-sm btn-outline-danger" type="submit">Hết hàng · Từ chối</button>
                                    </form>
                                <?php elseif ($item['status'] === 'approved'): ?>
                                    <form method="post">
                                        <input type="hidden" name="id" value="<?= (int)$item['id'] ?>">
                                        <input type="hidden" name="filter_status" value="<?= e($statusFilter) ?>">
                                        <input type="hidden" name="filter_date" value="<?= e($dateFilter) ?>">
                                        <button name="action" value="serve" class="btn btn-sm btn-primary" type="submit">Đã phục vụ</button>
                                    </form>
                                    <form method="post">
                                        <input type="hidden" name="id" value="<?= (int)$item['id'] ?>">
                                        <input type="hidden" name="filter_status" value="<?= e($statusFilter) ?>">
                                        <input type="hidden" name="filter_date" value="<?= e($dateFilter) ?>">
                                        <button name="action" value="reject" class="btn btn-sm btn-outline-danger" type="submit">Từ chối</button>
                                    </form>
                                <?php else: ?>
                                    <span class="badge status-<?= e($item['status']) ?>"><?= e(order_status_label($item['status'])) ?></span>
                                <?php endif; ?>
                            </div>
                        </div>
                    <?php endforeach; ?>
                </div>
            </article>
        <?php endforeach; ?>
    </div>
<?php endif; ?>
