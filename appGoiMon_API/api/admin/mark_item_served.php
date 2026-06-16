<?php

require_once __DIR__ . '/../../config/helpers.php';
require_once __DIR__ . '/_order_status_helper.php';

run_endpoint(function (): void {
    require_method('POST');
    update_order_item_status('served', 'Da danh dau mon da phuc vu');
});
