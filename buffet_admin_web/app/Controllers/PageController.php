<?php

require_once __DIR__ . '/../Core/Controller.php';

class PageController extends Controller
{
    private array $pages = [
        'dashboard' => [
            'title' => 'Dashboard',
            'subtitle' => 'Tong quan van hanh nha hang buffet',
            'view' => 'dashboard/index',
        ],
        'tables' => [
            'title' => 'Quan ly ban',
            'subtitle' => 'Theo doi trang thai ban theo thoi gian thuc',
            'view' => 'tables/index',
        ],
        'orders' => [
            'title' => 'Quan ly don hang',
            'subtitle' => 'Xu ly order, bep va thanh toan',
            'view' => 'orders/index',
        ],
        'menu' => [
            'title' => 'Quan ly menu',
            'subtitle' => 'Mon an, gia ban va tinh trang phuc vu',
            'view' => 'menu/index',
        ],
        'categories' => [
            'title' => 'Quan ly danh muc',
            'subtitle' => 'Nhom mon an va khu vuc hien thi',
            'view' => 'categories/index',
        ],
        'combos' => [
            'title' => 'Quan ly combo buffet',
            'subtitle' => 'Goi buffet, khung gio va gia ap dung',
            'view' => 'combos/index',
        ],
    ];

    public function show(string $page): void
    {
        $config = $this->pages[$page] ?? $this->pages['dashboard'];

        $this->view($config['view'], [
            'activePage' => $page,
            'pageTitle' => $config['title'],
            'pageSubtitle' => $config['subtitle'],
        ]);
    }
}
