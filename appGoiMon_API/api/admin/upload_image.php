<?php

require_once __DIR__ . '/../../config/helpers.php';

/**
 * Accepts a multipart/form-data upload with a single file field named `image`,
 * validates type/size by sniffing the real MIME, stores it under uploads/foods/
 * with a generated unique name, and returns the stored filename.
 *
 * The app saves this filename in menu_items.image; it is later served from
 * BASE_URL + "uploads/foods/{filename}".
 */
run_endpoint(function (): void {
    require_method('POST');

    if (!isset($_FILES['image']) || !is_array($_FILES['image'])) {
        json_response(false, 'Không nhận được file ảnh', null, 422);
    }

    $file = $_FILES['image'];

    if (($file['error'] ?? UPLOAD_ERR_NO_FILE) !== UPLOAD_ERR_OK) {
        json_response(false, 'Tải ảnh lên thất bại', null, 422);
    }

    $maxBytes = 5 * 1024 * 1024; // 5 MB
    if (($file['size'] ?? 0) <= 0 || $file['size'] > $maxBytes) {
        json_response(false, 'Ảnh phải nhỏ hơn 5MB', null, 422);
    }

    // Sniff the real MIME type — never trust the client-provided name/extension.
    $allowed = [
        'image/jpeg' => 'jpg',
        'image/png' => 'png',
        'image/webp' => 'webp',
    ];
    $finfo = new finfo(FILEINFO_MIME_TYPE);
    $mime = $finfo->file($file['tmp_name']);
    if (!isset($allowed[$mime])) {
        json_response(false, 'Chỉ chấp nhận ảnh JPG, PNG hoặc WEBP', null, 422);
    }
    $ext = $allowed[$mime];

    $dir = __DIR__ . '/../../uploads/foods';
    if (!is_dir($dir) && !mkdir($dir, 0775, true) && !is_dir($dir)) {
        json_response(false, 'Không tạo được thư mục lưu ảnh', null, 500);
    }

    $filename = 'food_' . date('YmdHis') . '_' . bin2hex(random_bytes(4)) . '.' . $ext;
    $target = $dir . '/' . $filename;

    if (!move_uploaded_file($file['tmp_name'], $target)) {
        json_response(false, 'Không lưu được ảnh', null, 500);
    }

    json_response(true, 'Đã tải ảnh lên', ['filename' => $filename], 201);
});
