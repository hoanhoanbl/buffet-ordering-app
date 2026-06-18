<?php

require_once __DIR__ . '/../Core/Controller.php';
require_once __DIR__ . '/../Core/Flash.php';
require_once __DIR__ . '/../Models/AdminModel.php';

class CategoriesController extends Controller
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

        $this->view('categories/index', [
            'activePage' => 'categories',
            'pageTitle' => 'Quản lý danh mục',
            'pageSubtitle' => 'Thêm, sửa, bật/tắt và xóa nhóm món ăn',
            'categories' => $this->model->categories(),
            'dbError' => $this->model->error(),
        ]);
    }

    private function handleAction(): void
    {
        $action = $_POST['action'] ?? '';
        $id = (int)($_POST['id'] ?? 0);
        $name = trim($_POST['name'] ?? '');
        $status = in_array($_POST['status'] ?? '', ['active', 'inactive'], true) ? $_POST['status'] : 'active';

        if (in_array($action, ['create', 'update'], true) && $name === '') {
            Flash::set('danger', 'Tên danh mục không được để trống.');
            $this->redirect('categories.php');
        }

        $ok = match ($action) {
            'create' => $this->model->createCategory(['category_name' => $name, 'status' => $status]),
            'update' => $id > 0 && $this->model->updateCategory(['id' => $id, 'category_name' => $name, 'status' => $status]),
            'toggle' => $id > 0 && $this->model->setCategoryStatus($id, $_POST['status'] ?? ''),
            'delete' => $id > 0 && $this->model->deleteCategory($id),
            default => false,
        };

        Flash::set($ok ? 'success' : 'danger', $ok ? 'Cập nhật danh mục thành công.' : 'Không thể cập nhật danh mục.');
        $this->redirect('categories.php');
    }
}
