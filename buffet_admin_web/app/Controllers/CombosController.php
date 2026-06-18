<?php

require_once __DIR__ . '/../Core/Controller.php';
require_once __DIR__ . '/../Core/Flash.php';
require_once __DIR__ . '/../Models/AdminModel.php';

class CombosController extends Controller
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

        $this->view('combos/index', [
            'activePage' => 'combos',
            'pageTitle' => 'Quản lý combo',
            'pageSubtitle' => 'Tạo combo buffet, gán món và giá theo người',
            'combos' => $this->model->combos(),
            'menuItems' => $this->model->menuItems(),
            'comboFoodMap' => $this->model->comboFoodIdMap(),
            'dbError' => $this->model->error(),
        ]);
    }

    private function handleAction(): void
    {
        $action = $_POST['action'] ?? '';
        $id = (int)($_POST['id'] ?? 0);

        if ($action === 'delete') {
            $ok = $this->model->deleteCombo($id);
            Flash::set($ok ? 'success' : 'danger', $ok ? 'Đã ẩn combo.' : 'Không thể xóa combo.');
            $this->redirect('combos.php');
        }

        $name = trim($_POST['name'] ?? '');
        $price = (float)($_POST['price'] ?? -1);

        if ($name === '' || $price <= 0) {
            Flash::set('danger', 'Vui lòng nhập tên combo và giá lớn hơn 0.');
            $this->redirect('combos.php');
        }

        $foodIds = array_map('intval', (array)($_POST['food_ids'] ?? []));

        $data = [
            'combo_name' => $name,
            'price_per_person' => $price,
            'description' => trim($_POST['description'] ?? ''),
            'status' => in_array($_POST['status'] ?? '', ['active', 'inactive'], true) ? $_POST['status'] : 'active',
            'food_ids' => $foodIds,
        ];

        $ok = $action === 'update'
            ? $this->model->updateCombo($data + ['id' => $id])
            : $this->model->createCombo($data);

        Flash::set($ok ? 'success' : 'danger', $ok ? 'Lưu combo thành công.' : 'Không thể lưu combo.');
        $this->redirect('combos.php');
    }
}
