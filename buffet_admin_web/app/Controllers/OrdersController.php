<?php

require_once __DIR__ . '/../Core/Controller.php';
require_once __DIR__ . '/../Core/Flash.php';
require_once __DIR__ . '/../Models/AdminModel.php';

class OrdersController extends Controller
{
    private AdminModel $model;

    public function __construct()
    {
        $this->model = new AdminModel();
    }

    public function index(): void
    {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $this->handleAction();
        }

        $status = $_GET['status'] ?? 'pending';
        if (!in_array($status, ['pending', 'approved', 'served', 'rejected'], true)) {
            $status = 'pending';
        }

        $date = trim($_GET['date'] ?? '');
        if ($date !== '' && preg_match('/^\d{4}-\d{2}-\d{2}$/', $date) !== 1) {
            $date = '';
        }

        $this->view('orders/index', [
            'activePage' => 'orders',
            'pageTitle' => 'Quản lý đơn hàng',
            'pageSubtitle' => 'Duyệt món, từ chối món và đánh dấu đã phục vụ',
            'items' => $this->model->orderItems($status, $date),
            'counts' => $this->model->orderItemCounts($date),
            'statusFilter' => $status,
            'dateFilter' => $date,
            'dbError' => $this->model->error(),
        ]);
    }

    private function handleAction(): void
    {
        $id = (int)($_POST['id'] ?? 0);
        $action = $_POST['action'] ?? '';
        $status = [
            'approve' => 'approved',
            'reject' => 'rejected',
            'serve' => 'served',
        ][$action] ?? '';

        $back = $this->backUrl();

        if ($id <= 0 || $status === '') {
            Flash::set('danger', 'Dữ liệu đơn hàng không hợp lệ.');
            $this->redirect($back);
        }

        $ok = $this->model->updateOrderItemStatus($id, $status);
        Flash::set($ok ? 'success' : 'danger', $ok ? 'Cập nhật trạng thái món thành công.' : 'Không thể cập nhật món.');
        $this->redirect($back);
    }

    /** Giữ nguyên bộ lọc trạng thái/ngày sau khi thao tác. */
    private function backUrl(): string
    {
        $params = [];
        $status = $_POST['filter_status'] ?? '';
        $date = $_POST['filter_date'] ?? '';

        if (in_array($status, ['pending', 'approved', 'served', 'rejected'], true)) {
            $params['status'] = $status;
        }
        if (preg_match('/^\d{4}-\d{2}-\d{2}$/', (string)$date) === 1) {
            $params['date'] = $date;
        }

        return 'orders.php' . ($params ? '?' . http_build_query($params) : '');
    }
}
