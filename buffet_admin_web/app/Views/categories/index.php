<?php
/** @var array $categories */
$categories = $categories ?? [];
?>
<section class="module-panel">
    <div class="module-toolbar">
        <div>
            <h2>Thêm danh mục</h2>
            <p>Danh mục giúp nhân viên tìm món nhanh trên POS.</p>
        </div>
    </div>
    <form id="create-form" class="crud-form compact" method="post">
        <input type="hidden" name="action" value="create">
        <input class="form-control" name="name" placeholder="Tên danh mục (VD: Món lẩu, Đồ uống...)" required>
        <select class="form-select" name="status">
            <option value="active">Đang hoạt động</option>
            <option value="inactive">Tạm ẩn</option>
        </select>
        <button class="btn btn-primary" type="submit"><i class="fa-solid fa-plus"></i> Thêm</button>
    </form>
</section>

<section class="module-panel mt-3">
    <div class="category-list">
        <?php foreach ($categories as $category): ?>
            <?php $isActive = ($category['status'] ?? 'active') === 'active'; ?>
            <div class="category-row <?= $isActive ? '' : 'is-inactive' ?>">
                <span class="cat-avatar <?= $isActive ? 'on' : 'off' ?>"><?= e(mb_strtoupper(mb_substr($category['name'], 0, 1))) ?></span>
                <form class="inline-edit grow" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="<?= (int)$category['id'] ?>">
                    <input class="form-control" name="name" value="<?= e($category['name']) ?>" required>
                    <select class="form-select" name="status">
                        <option value="active" <?= $isActive ? 'selected' : '' ?>>Hoạt động</option>
                        <option value="inactive" <?= !$isActive ? 'selected' : '' ?>>Tạm ẩn</option>
                    </select>
                    <button class="btn btn-sm btn-primary" type="submit">Lưu</button>
                </form>
                <span class="cat-count"><?= (int)($category['item_count'] ?? 0) ?> món</span>
                <span class="badge status-<?= $isActive ? 'available' : 'hidden' ?>"><?= $isActive ? 'Hoạt động' : 'Tạm ẩn' ?></span>
                <form method="post">
                    <input type="hidden" name="action" value="toggle">
                    <input type="hidden" name="id" value="<?= (int)$category['id'] ?>">
                    <input type="hidden" name="status" value="<?= $isActive ? 'inactive' : 'active' ?>">
                    <button class="btn btn-sm btn-outline-secondary" type="submit" title="Bật/tắt">
                        <i class="fa-solid fa-power-off"></i>
                    </button>
                </form>
                <form method="post" onsubmit="return confirm('Ẩn danh mục này?');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="<?= (int)$category['id'] ?>">
                    <button class="btn btn-sm btn-outline-danger" type="submit"><i class="fa-solid fa-trash"></i></button>
                </form>
            </div>
        <?php endforeach; ?>
        <?php if (!$categories): ?>
            <div class="empty-state">Chưa có danh mục.</div>
        <?php endif; ?>
    </div>
</section>
