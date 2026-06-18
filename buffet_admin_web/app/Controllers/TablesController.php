<?php

require_once __DIR__ . '/../Core/Controller.php';
require_once __DIR__ . '/../Core/Flash.php';
require_once __DIR__ . '/../Models/AdminModel.php';

class TablesController extends Controller
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

        $selectedId = (int)($_GET['id'] ?? 0);
        $search = trim($_GET['search'] ?? '');
        $status = $_GET['status'] ?? '';

        $this->view('tables/index', [
            'activePage' => 'tables',
            'pageTitle' => 'Quản lý bàn',
            'pageSubtitle' => 'Tìm kiếm, lọc trạng thái, xem order hiện tại và thanh toán',
            'tables' => $this->model->tables($search, $status),
            'selectedTable' => $selectedId ? $this->model->tableDetail($selectedId) : null,
            'tableItems' => $selectedId ? $this->model->tableItems($selectedId) : [],
            'search' => $search,
            'status' => $status,
            'dbError' => $this->model->error(),
        ]);
    }

    private function handleAction(): void
    {
        $id = (int)($_POST['id'] ?? 0);
        $action = $_POST['action'] ?? '';

        if ($id <= 0) {
            Flash::set('danger', 'Bàn không hợp lệ.');
            $this->redirect('tables.php');
        }

        $ok = match ($action) {
            'close' => $this->model->closeTable($id),
            'pay' => $this->model->confirmPayment($id),
            default => false,
        };

        if ($ok) {
            $successMessage = $action === 'pay' ? 'Đã xác nhận thanh toán và trả bàn.' : 'Đã đóng bàn thành công.';
            Flash::set('success', $successMessage);
        } else {
            // Ưu tiên thông báo nghiệp vụ từ model (vd: còn món chưa phục vụ).
            Flash::set('danger', $this->model->error() ?? 'Không thể cập nhật bàn.');
        }

        $this->redirect('tables.php?id=' . $id);
    }
}
