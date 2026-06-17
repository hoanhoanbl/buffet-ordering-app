<?php

require_once __DIR__ . '/../../config/helpers.php';

/**
 * Returns the menu_item_ids currently assigned to a combo — WITHOUT the availability filter
 * used by user/get_menu_by_combo.php, so the admin editor can pre-check every assigned dish
 * (including out-of-stock / hidden ones).
 */
run_endpoint(function (): void {
    require_method('GET');
    $comboId = int_param($_GET, 'combo_id');
    ensure_positive($comboId, 'Thiếu combo_id');

    $stmt = db()->prepare('SELECT menu_item_id FROM combo_menu_items WHERE combo_id = ?');
    $stmt->execute([$comboId]);

    json_response(true, 'Thành công', $stmt->fetchAll(PDO::FETCH_COLUMN));
});
