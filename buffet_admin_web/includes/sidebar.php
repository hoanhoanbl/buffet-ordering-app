<?php

$activePage = $activePage ?? 'dashboard';
$items = [
    'dashboard' => ['label' => 'Dashboard', 'href' => 'index.php', 'icon' => 'fa-solid fa-chart-line'],
    'tables' => ['label' => 'Quản lý bàn', 'href' => 'tables.php', 'icon' => 'fa-solid fa-table-cells-large'],
    'orders' => ['label' => 'Quản lý đơn hàng', 'href' => 'orders.php', 'icon' => 'fa-solid fa-receipt'],
    'menu' => ['label' => 'Quản lý menu', 'href' => 'menu.php', 'icon' => 'fa-solid fa-utensils'],
    'categories' => ['label' => 'Quản lý danh mục', 'href' => 'categories.php', 'icon' => 'fa-solid fa-tags'],
    'combos' => ['label' => 'Quản lý combo', 'href' => 'combos.php', 'icon' => 'fa-solid fa-layer-group'],
];
?>
<aside class="app-sidebar">
    <a class="sidebar-brand" href="index.php">
        <span class="brand-icon">BA</span>
        <span>
            <strong>Buffet Admin</strong>
            <small>Restaurant POS</small>
        </span>
    </a>
    <nav class="sidebar-nav">
        <?php foreach ($items as $key => $item): ?>
            <a class="<?= $activePage === $key ? 'active' : '' ?>" href="<?= $item['href'] ?>">
                <i class="<?= $item['icon'] ?>"></i>
                <span><?= $item['label'] ?></span>
            </a>
        <?php endforeach; ?>
    </nav>
    <a class="sidebar-logout" href="logout.php">
        <i class="fa-solid fa-arrow-right-from-bracket"></i>
        <span>Đăng xuất</span>
    </a>
</aside>
