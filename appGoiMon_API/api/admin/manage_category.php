<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();
    $action = str_param($params, 'action');

    if (!in_array($action, ['create', 'update', 'delete', 'set_status'], true)) {
        json_response(false, 'action must be create, update, delete, or set_status', null, 422);
    }

    if ($action === 'create') {
        $name = str_param($params, 'category_name', str_param($params, 'name'));
        $status = str_param($params, 'status', 'active');

        validate_category_status($status);
        if ($name === '') {
            json_response(false, 'Missing category name', null, 422);
        }

        $stmt = $pdo->prepare('INSERT INTO categories (category_name, status) VALUES (?, ?)');
        $stmt->execute([$name, $status]);

        json_response(true, 'Category created', ['category_id' => (int) $pdo->lastInsertId()], 201);
    }

    $categoryId = int_param($params, 'category_id');
    ensure_positive($categoryId, 'Missing category_id');

    if ($action === 'delete') {
        $stmt = $pdo->prepare("UPDATE categories SET status = 'inactive' WHERE id = ?");
        $stmt->execute([$categoryId]);
        json_response(true, 'Category deleted', ['category_id' => $categoryId, 'status' => 'inactive']);
    }

    if ($action === 'set_status') {
        $status = str_param($params, 'status');
        validate_category_status($status);

        $stmt = $pdo->prepare('UPDATE categories SET status = ? WHERE id = ?');
        $stmt->execute([$status, $categoryId]);
        json_response(true, 'Category status updated', ['category_id' => $categoryId, 'status' => $status]);
    }

    $name = str_param($params, 'category_name', str_param($params, 'name'));
    $status = str_param($params, 'status', 'active');

    validate_category_status($status);
    if ($name === '') {
        json_response(false, 'Missing category name', null, 422);
    }

    $stmt = $pdo->prepare('UPDATE categories SET category_name = ?, status = ? WHERE id = ?');
    $stmt->execute([$name, $status, $categoryId]);

    json_response(true, 'Category updated', ['category_id' => $categoryId]);
});

function validate_category_status(string $status): void
{
    if (!in_array($status, ['active', 'inactive'], true)) {
        json_response(false, 'Invalid category status', null, 422);
    }
}
