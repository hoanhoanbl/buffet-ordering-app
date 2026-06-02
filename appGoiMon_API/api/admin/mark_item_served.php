<?php

require_once __DIR__ . '/../../config/helpers.php';
require_once __DIR__ . '/_order_status_helper.php';

run_endpoint(function (): void {
    require_method('POST');
    update_order_item_status('served', 'Đã chuyển món sang đã phục vụ');
});
