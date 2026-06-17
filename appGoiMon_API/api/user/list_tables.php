<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $pdo = db();
    release_expired_sessions($pdo);

    // Current logged-in user. Each occupied table is flagged is_mine when its active session
    // belongs to this user, so the app can show "Bàn của bạn / Tiếp tục" instead of "Đang dùng".
    $userId = int_param($_GET, 'user_id');

    $stmt = $pdo->query(
        'SELECT t.id, t.table_code, t.table_name, t.status,
                ts.id AS session_id, ts.user_id AS session_user_id
         FROM restaurant_tables t
         LEFT JOIN table_sessions ts ON ts.id = (
             SELECT id FROM table_sessions
             WHERE table_id = t.id AND status = \'active\'
             ORDER BY id DESC LIMIT 1
         )
         ORDER BY t.id ASC'
    );

    $tables = array_map(
        function (array $table) use ($userId): array {
            $occupied = !empty($table['session_id']) || $table['status'] === 'occupied';
            $sessionOwner = isset($table['session_user_id']) ? (int) $table['session_user_id'] : 0;
            $isMine = $occupied && !empty($table['session_id'])
                && $userId > 0 && $sessionOwner === $userId;
            return [
                'id' => (int) $table['id'],
                'table_code' => $table['table_code'],
                'table_name' => $table['table_name'],
                'status' => $occupied ? 'occupied' : 'available',
                'is_mine' => $isMine,
            ];
        },
        $stmt->fetchAll()
    );

    json_response(true, 'Thành công', $tables);
});
