<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $pdo = db();
    $params = input();

    $username = str_param($params, 'username');
    $password = str_param($params, 'password');
    $fullName = str_param($params, 'full_name');
    $phone = str_param($params, 'phone');

    if ($username === '' || $password === '' || $fullName === '' || $phone === '') {
        json_response(false, 'Thiếu tên đăng nhập, mật khẩu, họ tên hoặc số điện thoại', null, 422);
    }

    if (strlen($password) < 6) {
        json_response(false, 'Mật khẩu phải có ít nhất 6 ký tự', null, 422);
    }

    $existsStmt = $pdo->prepare('SELECT username, phone FROM users WHERE username = ? OR phone = ? LIMIT 1');
    $existsStmt->execute([$username, $phone]);
    $existingUser = $existsStmt->fetch();

    if ($existingUser) {
        $message = $existingUser['username'] === $username
            ? 'Tên đăng nhập đã tồn tại'
            : 'Số điện thoại đã tồn tại';
        json_response(false, $message, null, 409);
    }

    $stmt = $pdo->prepare(
        "INSERT INTO users (username, password, full_name, phone, role)
         VALUES (?, ?, ?, ?, 'user')"
    );
    $stmt->execute([
        $username,
        password_hash($password, PASSWORD_DEFAULT),
        $fullName,
        $phone,
    ]);

    $user = find_user_by_username($pdo, $username);
    json_response(true, 'Đăng ký thành công', public_user($user), 201);
});
