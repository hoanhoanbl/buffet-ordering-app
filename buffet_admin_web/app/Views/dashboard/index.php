<?php
/** @var array $stats */
$stats = $stats ?? [];
$tables = $stats['tables'] ?? ['total' => 0, 'available' => 0, 'occupied' => 0];
$orderItems = $stats['orderItems'] ?? ['pending' => 0, 'approved' => 0, 'served' => 0, 'rejected' => 0];
$menu = $stats['menu'] ?? ['total' => 0, 'available' => 0, 'out_of_stock' => 0, 'hidden' => 0];
$cats = $stats['categories'] ?? ['total' => 0, 'active' => 0, 'inactive' => 0];
$combos = $stats['combos'] ?? ['total' => 0, 'active' => 0];

$pendingItems = (int)($orderItems['pending'] ?? 0);
$pendingPay = (int)($stats['pendingPaymentSessions'] ?? 0);
$allClear = $pendingItems === 0 && $pendingPay === 0;

$pct = static fn (int $part, int $total): float => $total > 0 ? round($part / $total * 100) : 0;
?>
<section class="dash-hero-grid">
    <div class="hero-card">
        <span class="hero-eyebrow"><i class="fa-solid fa-wallet"></i> Tổng doanh thu (đã thanh toán)</span>
        <strong class="hero-value"><?= money($stats['totalRevenue'] ?? 0) ?></strong>
        <div class="hero-substats">
            <div>
                <small>Doanh thu hôm nay</small>
                <span><?= money($stats['todayRevenue'] ?? 0) ?></span>
            </div>
            <div>
                <small>Phiên đang hoạt động</small>
                <span><?= (int)($stats['activeSessions'] ?? 0) ?></span>
            </div>
        </div>
    </div>

    <div class="attention-card <?= $allClear ? 'is-clear' : 'is-alert' ?>">
        <?php if ($allClear): ?>
            <i class="fa-solid fa-circle-check"></i>
            <strong>Mọi thứ ổn định</strong>
            <span>Không có món chờ duyệt hay bàn chờ thanh toán.</span>
        <?php else: ?>
            <i class="fa-solid fa-bell"></i>
            <strong>Cần xử lý</strong>
            <div class="attention-items">
                <a href="orders.php?status=pending"><?= $pendingItems ?> món chờ duyệt</a>
                <a href="tables.php"><?= $pendingPay ?> bàn chờ thanh toán</a>
            </div>
        <?php endif; ?>
    </div>
</section>

<section class="breakdown-grid">
    <div class="panel">
        <div class="panel-heading">
            <h2><i class="fa-solid fa-table-cells-large"></i> Bàn</h2>
            <a class="btn btn-sm btn-outline-secondary" href="tables.php">Xem bàn</a>
        </div>
        <div class="bd-total"><?= (int)$tables['total'] ?> bàn</div>
        <ul class="bd-list">
            <li><span class="dot dot-green"></span> Trống <b><?= (int)$tables['available'] ?></b></li>
            <li><span class="dot dot-orange"></span> Đang dùng <b><?= (int)$tables['occupied'] ?></b></li>
        </ul>
        <div class="bd-bar">
            <span class="seg seg-green" style="width: <?= $pct((int)$tables['available'], (int)$tables['total']) ?>%"></span>
            <span class="seg seg-orange" style="width: <?= $pct((int)$tables['occupied'], (int)$tables['total']) ?>%"></span>
        </div>
    </div>

    <div class="panel">
        <div class="panel-heading">
            <h2><i class="fa-solid fa-receipt"></i> Món trong đơn</h2>
            <a class="btn btn-sm btn-outline-secondary" href="orders.php">Duyệt món</a>
        </div>
        <ul class="bd-list two-col">
            <li><span class="dot dot-orange"></span> Chờ duyệt <b><?= (int)$orderItems['pending'] ?></b></li>
            <li><span class="dot dot-green"></span> Đã duyệt <b><?= (int)$orderItems['approved'] ?></b></li>
            <li><span class="dot dot-blue"></span> Đã phục vụ <b><?= (int)$orderItems['served'] ?></b></li>
            <li><span class="dot dot-red"></span> Từ chối <b><?= (int)$orderItems['rejected'] ?></b></li>
        </ul>
    </div>

    <div class="panel">
        <div class="panel-heading">
            <h2><i class="fa-solid fa-utensils"></i> Món ăn</h2>
            <a class="btn btn-sm btn-outline-secondary" href="menu.php">Quản lý</a>
        </div>
        <div class="bd-total"><?= (int)$menu['total'] ?> món</div>
        <ul class="bd-list">
            <li><span class="dot dot-green"></span> Đang bán <b><?= (int)$menu['available'] ?></b></li>
            <li><span class="dot dot-orange"></span> Tạm hết <b><?= (int)$menu['out_of_stock'] ?></b></li>
            <li><span class="dot dot-slate"></span> Đã ẩn <b><?= (int)$menu['hidden'] ?></b></li>
        </ul>
    </div>

    <div class="panel">
        <div class="panel-heading">
            <h2><i class="fa-solid fa-tags"></i> Danh mục & Combo</h2>
            <a class="btn btn-sm btn-outline-secondary" href="categories.php">Danh mục</a>
        </div>
        <ul class="bd-list">
            <li><span class="dot dot-green"></span> Danh mục hoạt động <b><?= (int)$cats['active'] ?></b></li>
            <li><span class="dot dot-slate"></span> Danh mục tạm ẩn <b><?= (int)$cats['inactive'] ?></b></li>
            <li><span class="dot dot-blue"></span> Combo đang bán <b><?= (int)$combos['active'] ?></b> / <?= (int)$combos['total'] ?></li>
        </ul>
    </div>
</section>

<section class="panel">
    <div class="panel-heading"><h2>Thao tác nhanh</h2></div>
    <div class="quick-actions">
        <a href="tables.php"><i class="fa-solid fa-table-cells-large"></i> Quản lý bàn</a>
        <a href="orders.php"><i class="fa-solid fa-receipt"></i> Duyệt món</a>
        <a href="menu.php"><i class="fa-solid fa-utensils"></i> Thêm món</a>
        <a href="combos.php"><i class="fa-solid fa-layer-group"></i> Combo buffet</a>
    </div>
</section>
