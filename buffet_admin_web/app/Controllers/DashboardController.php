<?php

require_once __DIR__ . '/../Core/Controller.php';
require_once __DIR__ . '/../Models/AdminModel.php';

class DashboardController extends Controller
{
    public function index(): void
    {
        $model = new AdminModel();

        $this->view('dashboard/index', [
            'activePage' => 'dashboard',
            'pageTitle' => 'Dashboard',
            'pageSubtitle' => 'Tổng quan vận hành nhà hàng buffet',
            'stats' => $model->dashboardStats(),
            'dbError' => $model->error(),
        ]);
    }
}
