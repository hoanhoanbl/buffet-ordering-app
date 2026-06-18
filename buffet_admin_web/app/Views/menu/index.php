<?php
/** @var array $items */
$items = $items ?? [];
$categories = $categories ?? [];
$search = $search ?? '';
$status = $status ?? '';
$statusOpts = ['available' => 'Đang bán', 'out_of_stock' => 'Tạm hết', 'hidden' => 'Đã ẩn'];
?>
<section class="module-panel">
    <form class="filter-bar" method="get">
        <div class="input-group">
            <span class="input-group-text"><i class="fa-solid fa-magnifying-glass"></i></span>
            <input class="form-control" name="search" value="<?= e($search) ?>" placeholder="Tìm theo tên món hoặc danh mục...">
        </div>
        <select class="form-select" name="status">
            <option value="">Tất cả trạng thái</option>
            <?php foreach ($statusOpts as $key => $label): ?>
                <option value="<?= $key ?>" <?= $status === $key ? 'selected' : '' ?>><?= $label ?></option>
            <?php endforeach; ?>
        </select>
        <button class="btn btn-primary" type="submit">Lọc</button>
        <?php if ($search !== '' || $status !== ''): ?>
            <a class="btn btn-outline-secondary" href="menu.php">Xóa lọc</a>
        <?php endif; ?>
    </form>
</section>

<section class="module-panel mt-3">
    <div class="module-toolbar">
        <div>
            <h2>Thêm món ăn</h2>
            <p>Chọn danh mục, tải ảnh, mô tả và trạng thái phục vụ.</p>
        </div>
    </div>
    <form id="create-form" class="crud-form" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="create">
        <input class="form-control" name="name" placeholder="Tên món" required>
        <select class="form-select" name="category_id" required>
            <option value="">Chọn danh mục</option>
            <?php foreach ($categories as $category): ?>
                <option value="<?= (int)$category['id'] ?>"><?= e($category['name']) ?></option>
            <?php endforeach; ?>
        </select>
        <input class="form-control" type="file" name="image" accept="image/png,image/jpeg,image/webp">
        <input class="form-control" name="description" placeholder="Mô tả (tùy chọn)">
        <select class="form-select" name="status">
            <?php foreach ($statusOpts as $key => $label): ?>
                <option value="<?= $key ?>"><?= $label ?></option>
            <?php endforeach; ?>
        </select>
        <button class="btn btn-primary" type="submit"><i class="fa-solid fa-plus"></i> Thêm món</button>
    </form>
    <?php if (!$categories): ?>
        <p class="text-muted mt-2 mb-0"><i class="fa-solid fa-circle-info"></i> Chưa có danh mục hoạt động. Hãy thêm danh mục trước.</p>
    <?php endif; ?>
</section>

<section class="module-panel mt-3">
    <div class="responsive-table">
        <table class="table align-middle action-table">
            <thead>
                <tr>
                    <th>Ảnh</th>
                    <th>Tên món & mô tả</th>
                    <th>Danh mục</th>
                    <th>Trạng thái</th>
                    <th>Lưu</th>
                    <th>Xóa</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($items as $item): ?>
                    <?php $img = img_url($item['image'] ?? ''); ?>
                    <tr>
                        <form method="post" enctype="multipart/form-data">
                            <td>
                                <?php if ($img !== ''): ?>
                                    <img class="food-thumb" src="<?= e($img) ?>" alt="">
                                <?php else: ?>
                                    <span class="food-thumb placeholder"><i class="fa-solid fa-image"></i></span>
                                <?php endif; ?>
                            </td>
                            <td>
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="id" value="<?= (int)$item['id'] ?>">
                                <input class="form-control form-control-sm" name="name" value="<?= e($item['name']) ?>" required>
                                <input class="form-control form-control-sm mt-1" name="description" value="<?= e($item['description'] ?? '') ?>" placeholder="Mô tả">
                            </td>
                            <td>
                                <select class="form-select form-select-sm" name="category_id" required>
                                    <?php foreach ($categories as $category): ?>
                                        <option value="<?= (int)$category['id'] ?>" <?= (int)$item['category_id'] === (int)$category['id'] ? 'selected' : '' ?>>
                                            <?= e($category['name']) ?>
                                        </option>
                                    <?php endforeach; ?>
                                    <?php if (!empty($item['category_name']) && !in_array((int)$item['category_id'], array_map(fn ($c) => (int)$c['id'], $categories), true)): ?>
                                        <option value="<?= (int)$item['category_id'] ?>" selected><?= e($item['category_name']) ?> (ẩn)</option>
                                    <?php endif; ?>
                                </select>
                            </td>
                            <td>
                                <select class="form-select form-select-sm" name="status">
                                    <?php foreach ($statusOpts as $key => $label): ?>
                                        <option value="<?= $key ?>" <?= $item['status'] === $key ? 'selected' : '' ?>><?= $label ?></option>
                                    <?php endforeach; ?>
                                </select>
                                <input class="form-control form-control-sm mt-1" type="file" name="image" accept="image/png,image/jpeg,image/webp">
                            </td>
                            <td><button class="btn btn-sm btn-primary" type="submit">Lưu</button></td>
                        </form>
                        <td>
                            <form method="post" onsubmit="return confirm('Ẩn món này khỏi menu?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="<?= (int)$item['id'] ?>">
                                <button class="btn btn-sm btn-outline-danger" type="submit">Ẩn</button>
                            </form>
                        </td>
                    </tr>
                <?php endforeach; ?>
                <?php if (!$items): ?>
                    <tr><td colspan="6" class="text-center text-muted py-4">Không có món ăn phù hợp.</td></tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</section>
