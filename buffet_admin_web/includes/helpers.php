<?php

/**
 * Tiện ích dùng chung cho view: cấu hình app, định dạng tiền, URL ảnh và nhãn trạng thái.
 */

function app_config(string $key, mixed $default = null): mixed
{
    static $config = null;
    if ($config === null) {
        $path = __DIR__ . '/../config/app.php';
        $config = is_file($path) ? require $path : [];
    }

    return $config[$key] ?? $default;
}

/** Định dạng tiền VND: 199000 -> "199.000đ". */
function money(int|float|string $value): string
{
    return number_format((float)$value, 0, ',', '.') . 'đ';
}

/** htmlspecialchars ngắn gọn. */
function e(?string $value): string
{
    return htmlspecialchars((string)$value, ENT_QUOTES, 'UTF-8');
}

/**
 * Trả về URL hiển thị ảnh từ giá trị cột image.
 * - rỗng           -> '' (view tự render placeholder)
 * - http(s)://...  -> giữ nguyên
 * - bắt đầu bằng / -> giữ nguyên (đường dẫn tuyệt đối cũ)
 * - còn lại        -> coi là tên file, ghép với image_base
 */
function img_url(?string $image): string
{
    $image = trim((string)$image);
    if ($image === '') {
        return '';
    }
    if (preg_match('#^https?://#i', $image) === 1 || $image[0] === '/') {
        return $image;
    }

    return rtrim((string)app_config('image_base', '/uploads/foods/'), '/') . '/' . $image;
}

/** Nhãn tiếng Việt cho trạng thái món trong đơn. */
function order_status_label(string $status): string
{
    return [
        'pending' => 'Chờ duyệt',
        'approved' => 'Đã duyệt',
        'served' => 'Đã phục vụ',
        'rejected' => 'Đã từ chối',
    ][$status] ?? $status;
}

/** Nhãn tiếng Việt cho trạng thái món trong menu. */
function menu_status_label(string $status): string
{
    return [
        'available' => 'Đang bán',
        'out_of_stock' => 'Tạm hết',
        'hidden' => 'Đã ẩn',
    ][$status] ?? $status;
}
