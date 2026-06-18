<?php

session_start();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $_SESSION['admin_logged_in'] = true;
    header('Location: index.php');
    exit;
}
?>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dang nhap | Buffet Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/css/app.css" rel="stylesheet">
</head>
<body class="login-page">
    <main class="login-panel">
        <div class="brand-mark">BA</div>
        <h1>Buffet Admin</h1>
        <p>Dang nhap he thong POS nha hang</p>
        <form method="post" class="vstack gap-3">
            <input class="form-control form-control-lg" type="text" name="username" placeholder="Tai khoan">
            <input class="form-control form-control-lg" type="password" name="password" placeholder="Mat khau">
            <button class="btn btn-primary btn-lg" type="submit">Dang nhap</button>
        </form>
    </main>
</body>
</html>
