<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $comboId = int_param($_GET, 'combo_id');
    ensure_positive($comboId, 'Thiếu combo_id');

    $stmt = db()->prepare(
        "SELECT f.id, f.category_id, ca.category_name, f.item_name AS name, f.image, f.description, f.status
         FROM combo_menu_items cf
         JOIN menu_items f ON f.id = cf.menu_item_id
         LEFT JOIN categories ca ON ca.id = f.category_id
         WHERE cf.combo_id = ? AND f.status = 'available'
         ORDER BY ca.id ASC, f.item_name ASC"
    );
    $stmt->execute([$comboId]);

    json_response(true, 'Thành công', $stmt->fetchAll());
});
