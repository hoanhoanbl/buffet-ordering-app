<?php

require_once __DIR__ . '/../../config/helpers.php';

/**
 * Admin combo list — ALL combos (active + inactive) with how many dishes each contains.
 * (The user-facing user/get_combos.php only returns active combos and synthesizes banner images.)
 */
run_endpoint(function (): void {
    require_method('GET');

    $stmt = db()->query(
        "SELECT c.id, c.combo_name, c.price_per_person, c.description, c.status,
                COUNT(cmi.menu_item_id) AS item_count
         FROM buffet_combos c
         LEFT JOIN combo_menu_items cmi ON cmi.combo_id = c.id
         GROUP BY c.id, c.combo_name, c.price_per_person, c.description, c.status
         ORDER BY c.price_per_person ASC, c.id ASC"
    );

    json_response(true, 'Thành công', $stmt->fetchAll());
});
