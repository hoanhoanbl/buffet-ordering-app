<?php
/** @var array $combos */
$combos = $combos ?? [];
$menuItems = $menuItems ?? [];
$comboFoodMap = $comboFoodMap ?? [];

// Gom món theo danh mục để hiển thị trong bộ chọn.
$grouped = [];
foreach ($menuItems as $mi) {
    $grouped[$mi['category_name'] ?? 'Khác'][] = $mi;
}

/** Render bộ chọn món (checkbox) cho 1 form combo. $checked = mảng id đã gán. */
$renderPicker = static function (array $grouped, array $checked, string $uid) {
    $checkedSet = array_flip(array_map('intval', $checked));
    ?>
    <div class="food-picker" data-picker>
        <input class="form-control form-control-sm food-picker-search" type="text" placeholder="Tìm món..." oninput="filterPicker(this)">
        <div class="food-picker-body">
            <?php foreach ($grouped as $catName => $foods): ?>
                <div class="fp-group">
                    <div class="fp-group-title"><?= e($catName) ?></div>
                    <?php foreach ($foods as $f): ?>
                        <label class="fp-item" data-name="<?= e(mb_strtolower($f['name'])) ?>">
                            <input type="checkbox" name="food_ids[]" value="<?= (int)$f['id'] ?>"
                                <?= isset($checkedSet[(int)$f['id']]) ? 'checked' : '' ?>>
                            <span><?= e($f['name']) ?></span>
                            <?php if (($f['status'] ?? '') !== 'available'): ?>
                                <small class="fp-muted">(<?= e(menu_status_label($f['status'] ?? '')) ?>)</small>
                            <?php endif; ?>
                        </label>
                    <?php endforeach; ?>
                </div>
            <?php endforeach; ?>
            <?php if (!$grouped): ?>
                <p class="text-muted m-2">Chưa có món nào. Hãy thêm món trong mục Quản lý menu.</p>
            <?php endif; ?>
        </div>
    </div>
    <?php
};
?>
<section class="module-panel">
    <div class="module-toolbar">
        <div>
            <h2>Thêm combo buffet</h2>
            <p>Đặt tên, giá theo người, chọn các món thuộc combo.</p>
        </div>
    </div>
    <form id="create-form" class="crud-form vertical" method="post">
        <input type="hidden" name="action" value="create">
        <div class="cf-row">
            <input class="form-control" name="name" placeholder="Tên combo (VD: Combo tự do 209)" required>
            <input class="form-control" type="number" min="1000" step="1000" name="price" placeholder="Giá / người" required>
            <select class="form-select" name="status">
                <option value="active">Đang bán</option>
                <option value="inactive">Tạm ngưng</option>
            </select>
        </div>
        <input class="form-control" name="description" placeholder="Mô tả (tùy chọn)">
        <details class="picker-details">
            <summary>Chọn món trong combo</summary>
            <?php $renderPicker($grouped, [], 'new'); ?>
        </details>
        <div>
            <button class="btn btn-primary" type="submit"><i class="fa-solid fa-plus"></i> Thêm combo</button>
        </div>
    </form>
</section>

<section class="combo-grid mt-3">
    <?php foreach ($combos as $combo): ?>
        <?php
        $isActive = ($combo['status'] ?? 'active') === 'active';
        $checked = $comboFoodMap[(int)$combo['id']] ?? [];
        ?>
        <article class="combo-card <?= $isActive ? '' : 'is-inactive' ?>">
            <header class="combo-card-head">
                <span class="badge status-<?= $isActive ? 'available' : 'hidden' ?>"><?= $isActive ? 'Đang bán' : 'Tạm ngưng' ?></span>
                <span class="combo-count"><i class="fa-solid fa-utensils"></i> <?= count($checked) ?> món</span>
            </header>
            <form method="post" class="vstack gap-2">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="<?= (int)$combo['id'] ?>">
                <input class="form-control" name="name" value="<?= e($combo['name']) ?>" required>
                <div class="cf-row">
                    <input class="form-control" type="number" min="1000" step="1000" name="price" value="<?= (int)$combo['price'] ?>" required>
                    <select class="form-select" name="status">
                        <option value="active" <?= $isActive ? 'selected' : '' ?>>Đang bán</option>
                        <option value="inactive" <?= !$isActive ? 'selected' : '' ?>>Tạm ngưng</option>
                    </select>
                </div>
                <input class="form-control" name="description" value="<?= e($combo['description'] ?? '') ?>" placeholder="Mô tả">
                <details class="picker-details">
                    <summary>Món trong combo (<?= count($checked) ?>)</summary>
                    <?php $renderPicker($grouped, $checked, 'c' . (int)$combo['id']); ?>
                </details>
                <div class="row-actions">
                    <button class="btn btn-sm btn-primary" type="submit">Lưu</button>
                </div>
            </form>
            <form method="post" onsubmit="return confirm('Ẩn combo này?');">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="<?= (int)$combo['id'] ?>">
                <button class="btn btn-sm btn-outline-danger w-100" type="submit"><i class="fa-solid fa-eye-slash"></i> Ẩn combo</button>
            </form>
        </article>
    <?php endforeach; ?>
    <?php if (!$combos): ?>
        <div class="module-panel empty-state">Chưa có combo buffet.</div>
    <?php endif; ?>
</section>

<script>
function filterPicker(input) {
    var q = input.value.trim().toLowerCase();
    var body = input.closest('[data-picker]');
    body.querySelectorAll('.fp-item').forEach(function (el) {
        el.style.display = el.getAttribute('data-name').indexOf(q) !== -1 ? '' : 'none';
    });
    body.querySelectorAll('.fp-group').forEach(function (g) {
        var any = Array.prototype.some.call(g.querySelectorAll('.fp-item'), function (i) { return i.style.display !== 'none'; });
        g.style.display = any ? '' : 'none';
    });
}
</script>
