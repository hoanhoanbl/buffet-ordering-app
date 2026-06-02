<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $stmt = db()->query(
        "SELECT id, name, price_per_person, description, status
         FROM buffet_combos
         WHERE status = 'active'
         ORDER BY price_per_person ASC, id ASC"
    );

    json_response(true, 'Thành công', $stmt->fetchAll());
});
