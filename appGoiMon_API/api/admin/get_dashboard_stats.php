<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $pdo = db();

    $revenueStmt = $pdo->query(
        "SELECT
            COALESCE(SUM(CASE WHEN payment_status = 'paid' THEN total_amount ELSE 0 END), 0) AS total_revenue,
            COALESCE(SUM(CASE WHEN payment_status = 'paid' AND DATE(start_time) = CURDATE() THEN total_amount ELSE 0 END), 0) AS today_revenue,
            SUM(status = 'active') AS active_sessions,
            SUM(status = 'expired') AS pending_payment_sessions
         FROM table_sessions"
    );
    $revenue = $revenueStmt->fetch() ?: [];

    $tableStmt = $pdo->query(
        "SELECT
            COUNT(*) AS total,
            SUM(status = 'available') AS available,
            SUM(status = 'occupied') AS occupied,
            0 AS waiting_payment
         FROM restaurant_tables"
    );
    $tables = $tableStmt->fetch() ?: [];

    $orderStmt = $pdo->query(
        "SELECT
            SUM(status = 'pending') AS pending,
            SUM(status = 'approved') AS approved,
            SUM(status = 'served') AS served,
            SUM(status = 'rejected') AS rejected
         FROM order_items"
    );
    $orderItems = $orderStmt->fetch() ?: [];

    $menuStmt = $pdo->query(
        "SELECT
            COUNT(*) AS total,
            SUM(status = 'available') AS available,
            SUM(status = 'out_of_stock') AS out_of_stock,
            SUM(status = 'hidden') AS hidden
         FROM menu_items"
    );
    $menuItems = $menuStmt->fetch() ?: [];

    $categoryStmt = $pdo->query(
        "SELECT
            COUNT(*) AS total,
            SUM(status = 'active') AS active,
            SUM(status = 'inactive') AS inactive
         FROM categories"
    );
    $categories = $categoryStmt->fetch() ?: [];

    json_response(true, 'Success', [
        'total_revenue' => (string) ($revenue['total_revenue'] ?? '0'),
        'today_revenue' => (string) ($revenue['today_revenue'] ?? '0'),
        'active_sessions' => (int) ($revenue['active_sessions'] ?? 0),
        'pending_payment_sessions' => (int) ($revenue['pending_payment_sessions'] ?? 0),
        'tables' => [
            'total' => (int) ($tables['total'] ?? 0),
            'available' => (int) ($tables['available'] ?? 0),
            'occupied' => (int) ($tables['occupied'] ?? 0),
            'waiting_payment' => (int) ($tables['waiting_payment'] ?? 0),
        ],
        'order_items' => [
            'pending' => (int) ($orderItems['pending'] ?? 0),
            'processing' => (int) ($orderItems['approved'] ?? 0),
            'approved' => (int) ($orderItems['approved'] ?? 0),
            'served' => (int) ($orderItems['served'] ?? 0),
            'rejected' => (int) ($orderItems['rejected'] ?? 0),
        ],
        'menu_items' => [
            'total' => (int) ($menuItems['total'] ?? 0),
            'available' => (int) ($menuItems['available'] ?? 0),
            'out_of_stock' => (int) ($menuItems['out_of_stock'] ?? 0),
            'hidden' => (int) ($menuItems['hidden'] ?? 0),
        ],
        'categories' => [
            'total' => (int) ($categories['total'] ?? 0),
            'active' => (int) ($categories['active'] ?? 0),
            'inactive' => (int) ($categories['inactive'] ?? 0),
        ],
    ]);
});
