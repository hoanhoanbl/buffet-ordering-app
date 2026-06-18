<?php
require_once __DIR__ . '/../../Core/Flash.php';
$flash = Flash::get();
require __DIR__ . '/../../../includes/header.php';
?>
<div class="app-shell">
    <?php require __DIR__ . '/../../../includes/sidebar.php'; ?>
    <main class="app-main">
        <header class="topbar">
            <div>
                <p class="eyebrow">Buffet operations</p>
                <h1><?= htmlspecialchars($pageTitle ?? 'Dashboard') ?></h1>
                <p><?= htmlspecialchars($pageSubtitle ?? '') ?></p>
            </div>
            <div class="topbar-actions">
                <button class="btn btn-light" type="button"><i class="fa-solid fa-bell"></i></button>
                <a class="btn btn-primary" href="<?= htmlspecialchars($activePage ?? 'dashboard') === 'dashboard' ? 'orders.php' : '#create-form' ?>">
                    <i class="fa-solid fa-plus"></i> Tạo mới
                </a>
            </div>
        </header>
        <?php if (!empty($dbError)): ?>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <?= htmlspecialchars($dbError) ?>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <?php endif; ?>
        <?php if ($flash): ?>
            <div class="alert alert-<?= htmlspecialchars($flash['type']) ?> alert-dismissible fade show" role="alert">
                <?= htmlspecialchars($flash['message']) ?>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <?php endif; ?>
        <?= $content ?>
    </main>
</div>
<?php require __DIR__ . '/../../../includes/footer.php'; ?>
