<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('POST');
    $params = input();
    $username = str_param($params, 'username');
    $password = str_param($params, 'password');

    if ($username === '' || $password === '') {
        json_response(false, 'Thiếu tên đăng nhập hoặc mật khẩu', null, 422);
    }

    $stmt = db()->prepare("SELECT id, username, password, role FROM users WHERE username = ? AND role = 'admin' LIMIT 1");
    $stmt->execute([$username]);
    $user = $stmt->fetch();

    if (!$user || ((string) $user['password'] !== $password && !password_verify($password, (string) $user['password']))) {
        json_response(false, 'Sai thông tin đăng nhập', null, 401);
    }

    unset($user['password']);
    json_response(true, 'Đăng nhập thành công', $user);
});
