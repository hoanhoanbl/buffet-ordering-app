<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $action = str_param($params, 'action');

    if (!in_array($action, ['create', 'update', 'delete', 'set_status'], true)) {
        json_response(false, 'action phải là create, update, delete hoặc set_status', null, 422);
    }

    if ($action === 'create') {
        $categoryId = int_param($params, 'category_id');
        $name = str_param($params, 'name');
        $image = str_param($params, 'image');
        $description = str_param($params, 'description');
        $status = str_param($params, 'status', 'available');

        ensure_positive($categoryId, 'Thiếu category_id');
        if ($name === '') {
            json_response(false, 'Thiếu tên món', null, 422);
        }

        $stmt = $pdo->prepare('INSERT INTO foods (category_id, name, image, description, status) VALUES (?, ?, ?, ?, ?)');
        $stmt->execute([$categoryId, $name, $image, $description, $status]);
        json_response(true, 'Đã thêm món', ['food_id' => (int) $pdo->lastInsertId()], 201);
    }

    $foodId = int_param($params, 'food_id');
    ensure_positive($foodId, 'Thiếu food_id');

    if ($action === 'delete') {
        $stmt = $pdo->prepare("UPDATE foods SET status = 'deleted' WHERE id = ?");
        $stmt->execute([$foodId]);
        json_response(true, 'Đã xóa món', ['food_id' => $foodId, 'status' => 'deleted']);
    }

    if ($action === 'set_status') {
        $status = str_param($params, 'status');
        if ($status === '') {
            json_response(false, 'Thiếu status', null, 422);
        }
        $stmt = $pdo->prepare('UPDATE foods SET status = ? WHERE id = ?');
        $stmt->execute([$status, $foodId]);
        json_response(true, 'Đã cập nhật trạng thái món', ['food_id' => $foodId, 'status' => $status]);
    }

    $categoryId = int_param($params, 'category_id');
    $name = str_param($params, 'name');
    $image = str_param($params, 'image');
    $description = str_param($params, 'description');
    $status = str_param($params, 'status', 'available');

    ensure_positive($categoryId, 'Thiếu category_id');
    if ($name === '') {
        json_response(false, 'Thiếu tên món', null, 422);
    }

    $stmt = $pdo->prepare(
        'UPDATE foods
         SET category_id = ?, name = ?, image = ?, description = ?, status = ?
         WHERE id = ?'
    );
    $stmt->execute([$categoryId, $name, $image, $description, $status, $foodId]);

    json_response(true, 'Đã cập nhật món', ['food_id' => $foodId]);
});
