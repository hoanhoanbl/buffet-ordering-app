<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $stmt = db()->query(
        'SELECT id, category_name, status
         FROM categories
         ORDER BY status ASC, category_name ASC, id ASC'
    );

    json_response(true, 'Success', $stmt->fetchAll());
});
