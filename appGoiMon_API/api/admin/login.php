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

    $user = find_user_by_username(db(), $username);

    if (!$user || $user['role'] !== 'admin' || !password_matches($password, (string) $user['password'])) {
        json_response(false, 'Sai thông tin đăng nhập', null, 401);
    }

    json_response(true, 'Đăng nhập thành công', public_user($user));
});
