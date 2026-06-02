<?php

declare(strict_types=1);

require_once __DIR__ . '/database.php';

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

function json_response(bool $success, string $message, mixed $data = null, int $statusCode = 200): void
{
    http_response_code($statusCode);
    echo json_encode([
        'success' => $success,
        'message' => $message,
        'data' => $data,
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

function require_method(string $method): void
{
    if ($_SERVER['REQUEST_METHOD'] !== strtoupper($method)) {
        json_response(false, 'Phương thức không hợp lệ', null, 405);
    }
}

function input(): array
{
    $raw = file_get_contents('php://input');
    $json = json_decode($raw ?: '', true);

    if (is_array($json)) {
        return $json;
    }

    return $_POST ?: $_GET ?: [];
}

function int_param(array $source, string $key, int $default = 0): int
{
    return isset($source[$key]) ? (int) $source[$key] : $default;
}

function str_param(array $source, string $key, string $default = ''): string
{
    return isset($source[$key]) ? trim((string) $source[$key]) : $default;
}

function active_session_condition(string $alias = 'ts'): string
{
    return "{$alias}.status IN ('pending_payment', 'active', 'checkout_requested')";
}

function fetch_table(PDO $pdo, array $params): ?array
{
    $tableId = int_param($params, 'table_id');
    $tableCode = str_param($params, 'table_code');

    if ($tableId > 0) {
        $stmt = $pdo->prepare('SELECT * FROM tables WHERE id = ?');
        $stmt->execute([$tableId]);
        $table = $stmt->fetch();
        return $table ?: null;
    }

    if ($tableCode !== '') {
        $stmt = $pdo->prepare('SELECT * FROM tables WHERE table_code = ?');
        $stmt->execute([$tableCode]);
        $table = $stmt->fetch();
        return $table ?: null;
    }

    return null;
}

function ensure_positive(int $value, string $message): void
{
    if ($value <= 0) {
        json_response(false, $message, null, 422);
    }
}

function run_endpoint(callable $handler): void
{
    try {
        $handler();
    } catch (PDOException $e) {
        json_response(false, 'Lỗi cơ sở dữ liệu', ['error' => $e->getMessage()], 500);
    } catch (Throwable $e) {
        json_response(false, 'Lỗi hệ thống', ['error' => $e->getMessage()], 500);
    }
}
