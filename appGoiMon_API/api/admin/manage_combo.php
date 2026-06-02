<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $action = str_param($params, 'action');

    if (!in_array($action, ['create', 'update', 'delete', 'set_foods'], true)) {
        json_response(false, 'action phải là create, update, delete hoặc set_foods', null, 422);
    }

    if ($action === 'create' || $action === 'update') {
        $name = str_param($params, 'name');
        $price = int_param($params, 'price_per_person');
        $description = str_param($params, 'description');
        $status = str_param($params, 'status', 'active');
        $foodIds = $params['food_ids'] ?? [];

        if ($name === '') {
            json_response(false, 'Thiếu tên combo', null, 422);
        }
        ensure_positive($price, 'Giá combo phải lớn hơn 0');

        $pdo->beginTransaction();
        if ($action === 'create') {
            $stmt = $pdo->prepare('INSERT INTO buffet_combos (name, price_per_person, description, status) VALUES (?, ?, ?, ?)');
            $stmt->execute([$name, $price, $description, $status]);
            $comboId = (int) $pdo->lastInsertId();
        } else {
            $comboId = int_param($params, 'combo_id');
            ensure_positive($comboId, 'Thiếu combo_id');
            $stmt = $pdo->prepare('UPDATE buffet_combos SET name = ?, price_per_person = ?, description = ?, status = ? WHERE id = ?');
            $stmt->execute([$name, $price, $description, $status, $comboId]);
        }

        if (is_array($foodIds)) {
            sync_combo_foods($pdo, $comboId, $foodIds);
        }

        $pdo->commit();
        json_response(true, $action === 'create' ? 'Đã thêm combo' : 'Đã cập nhật combo', ['combo_id' => $comboId], $action === 'create' ? 201 : 200);
    }

    $comboId = int_param($params, 'combo_id');
    ensure_positive($comboId, 'Thiếu combo_id');

    if ($action === 'delete') {
        $stmt = $pdo->prepare("UPDATE buffet_combos SET status = 'deleted' WHERE id = ?");
        $stmt->execute([$comboId]);
        json_response(true, 'Đã xóa combo', ['combo_id' => $comboId, 'status' => 'deleted']);
    }

    $foodIds = $params['food_ids'] ?? [];
    if (!is_array($foodIds)) {
        json_response(false, 'food_ids phải là mảng', null, 422);
    }

    $pdo->beginTransaction();
    sync_combo_foods($pdo, $comboId, $foodIds);
    $pdo->commit();

    json_response(true, 'Đã gán món vào combo', ['combo_id' => $comboId, 'food_ids' => array_values(array_unique(array_map('intval', $foodIds)))]);
});

function sync_combo_foods(PDO $pdo, int $comboId, array $foodIds): void
{
    $pdo->prepare('DELETE FROM combo_foods WHERE combo_id = ?')->execute([$comboId]);
    $insert = $pdo->prepare('INSERT INTO combo_foods (combo_id, food_id) VALUES (?, ?)');
    $uniqueFoodIds = array_values(array_unique(array_filter(array_map('intval', $foodIds), fn (int $id): bool => $id > 0)));

    foreach ($uniqueFoodIds as $foodId) {
        $insert->execute([$comboId, $foodId]);
    }
}
