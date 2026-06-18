<?php

require_once __DIR__ . '/../Core/Controller.php';
require_once __DIR__ . '/../Core/Flash.php';
require_once __DIR__ . '/../Models/AdminModel.php';

class MenuController extends Controller
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

        $search = trim($_GET['search'] ?? '');
        $status = $_GET['status'] ?? '';
        if (!in_array($status, ['available', 'out_of_stock', 'hidden'], true)) {
            $status = '';
        }

        $this->view('menu/index', [
            'activePage' => 'menu',
            'pageTitle' => 'Quản lý menu',
            'pageSubtitle' => 'Thêm/sửa món, đổi trạng thái, tải ảnh và tìm kiếm',
            'items' => $this->model->menuItems($search, $status),
            'categories' => $this->model->activeCategories(),
            'search' => $search,
            'status' => $status,
            'dbError' => $this->model->error(),
        ]);
    }

    private function handleAction(): void
    {
        $action = $_POST['action'] ?? '';
        $id = (int)($_POST['id'] ?? 0);

        if ($action === 'delete') {
            $ok = $this->model->deleteMenuItem($id);
            Flash::set($ok ? 'success' : 'danger', $ok ? 'Đã ẩn món ăn.' : 'Không thể xóa món ăn.');
            $this->redirect('menu.php');
        }

        if ($action === 'set_status') {
            $ok = $this->model->setMenuItemStatus($id, $_POST['status'] ?? '');
            Flash::set($ok ? 'success' : 'danger', $ok ? 'Đã cập nhật trạng thái món.' : 'Không thể cập nhật trạng thái.');
            $this->redirect('menu.php');
        }

        $name = trim($_POST['name'] ?? '');
        $categoryId = (int)($_POST['category_id'] ?? 0);

        if ($name === '' || $categoryId <= 0) {
            Flash::set('danger', 'Vui lòng nhập tên món và chọn danh mục.');
            $this->redirect('menu.php');
        }

        $data = [
            'category_id' => $categoryId,
            'item_name' => $name,
            'description' => trim($_POST['description'] ?? ''),
            'image' => $this->uploadImage(),
            'status' => $_POST['status'] ?? 'available',
        ];

        if ($action === 'update') {
            // Ảnh rỗng khi sửa => model giữ nguyên ảnh cũ.
            $ok = $this->model->updateMenuItem($data + ['id' => $id]);
        } else {
            $ok = $this->model->createMenuItem($data);
        }

        Flash::set($ok ? 'success' : 'danger', $ok ? 'Lưu món ăn thành công.' : 'Không thể lưu món ăn.');
        $this->redirect('menu.php');
    }

    /**
     * Tải ảnh lên thư mục uploads/foods, trả về TÊN FILE (giống cách app/API lưu).
     * Hiển thị qua helper img_url(). Trả '' nếu không chọn ảnh.
     */
    private function uploadImage(): string
    {
        if (empty($_FILES['image']['name']) || ($_FILES['image']['error'] ?? UPLOAD_ERR_NO_FILE) !== UPLOAD_ERR_OK) {
            return '';
        }

        $allowed = ['jpg', 'jpeg', 'png', 'webp'];
        $extension = strtolower(pathinfo($_FILES['image']['name'], PATHINFO_EXTENSION));

        if (!in_array($extension, $allowed, true)) {
            Flash::set('danger', 'Ảnh món ăn chỉ hỗ trợ JPG, PNG hoặc WEBP.');
            $this->redirect('menu.php');
        }

        $config = require __DIR__ . '/../../config/app.php';
        $dir = !empty($config['upload_dir']) ? $config['upload_dir'] : __DIR__ . '/../../uploads/foods';

        if (!is_dir($dir) && !mkdir($dir, 0775, true) && !is_dir($dir)) {
            Flash::set('danger', 'Không tạo được thư mục lưu ảnh.');
            $this->redirect('menu.php');
        }

        $fileName = 'food_' . date('YmdHis') . '_' . bin2hex(random_bytes(4)) . '.' . $extension;

        return move_uploaded_file($_FILES['image']['tmp_name'], $dir . '/' . $fileName)
            ? $fileName
            : '';
    }
}
