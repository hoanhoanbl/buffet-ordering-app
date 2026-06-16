<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $stmt = db()->query(
        'SELECT
            f.id,
            f.category_id,
            ca.category_name,
            f.item_name AS name,
            f.image,
            f.description,
            f.status
         FROM menu_items f
         LEFT JOIN categories ca ON ca.id = f.category_id
         ORDER BY ca.category_name ASC, f.item_name ASC, f.id ASC'
    );

    json_response(true, 'Success', $stmt->fetchAll());
});
