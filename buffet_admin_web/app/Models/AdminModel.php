<?php

require_once __DIR__ . '/Database.php';

class AdminModel
{
    private ?PDO $db = null;
    private ?string $error = null;

    public function __construct()
    {
        try {
            $this->db = Database::connect();
        } catch (Throwable $e) {
            $this->error = $e->getMessage();
        }
    }

    public function error(): ?string
    {
        return $this->error;
    }

    public function dashboardStats(): array
    {
        $revenue = $this->fetchOne(
            "SELECT
                COALESCE(SUM(CASE WHEN payment_status = 'paid' THEN total_amount ELSE 0 END), 0) AS total_revenue,
                COALESCE(SUM(CASE WHEN payment_status = 'paid' AND DATE(start_time) = CURDATE() THEN total_amount ELSE 0 END), 0) AS today_revenue,
                SUM(status = 'active') AS active_sessions,
                SUM(status = 'expired') AS pending_payment_sessions
             FROM table_sessions"
        ) ?? [];

        $tables = $this->fetchOne(
            "SELECT
                COUNT(*) AS total,
                SUM(status = 'available') AS available,
                SUM(status = 'occupied') AS occupied
             FROM restaurant_tables"
        ) ?? [];

        $orderItems = $this->fetchOne(
            "SELECT
                SUM(status = 'pending') AS pending,
                SUM(status = 'approved') AS approved,
                SUM(status = 'served') AS served,
                SUM(status = 'rejected') AS rejected
             FROM order_items"
        ) ?? [];

        $menu = $this->fetchOne(
            "SELECT
                COUNT(*) AS total,
                SUM(status = 'available') AS available,
                SUM(status = 'out_of_stock') AS out_of_stock,
                SUM(status = 'hidden') AS hidden
             FROM menu_items"
        ) ?? [];

        $categories = $this->fetchOne(
            "SELECT
                COUNT(*) AS total,
                SUM(status = 'active') AS active,
                SUM(status = 'inactive') AS inactive
             FROM categories"
        ) ?? [];

        $combos = $this->fetchOne(
            "SELECT
                COUNT(*) AS total,
                SUM(status = 'active') AS active
             FROM buffet_combos"
        ) ?? [];

        return [
            'totalRevenue' => (float)($revenue['total_revenue'] ?? 0),
            'todayRevenue' => (float)($revenue['today_revenue'] ?? 0),
            'activeSessions' => (int)($revenue['active_sessions'] ?? 0),
            'pendingPaymentSessions' => (int)($revenue['pending_payment_sessions'] ?? 0),
            'tables' => [
                'total' => (int)($tables['total'] ?? 0),
                'available' => (int)($tables['available'] ?? 0),
                'occupied' => (int)($tables['occupied'] ?? 0),
            ],
            'orderItems' => [
                'pending' => (int)($orderItems['pending'] ?? 0),
                'approved' => (int)($orderItems['approved'] ?? 0),
                'served' => (int)($orderItems['served'] ?? 0),
                'rejected' => (int)($orderItems['rejected'] ?? 0),
            ],
            'menu' => [
                'total' => (int)($menu['total'] ?? 0),
                'available' => (int)($menu['available'] ?? 0),
                'out_of_stock' => (int)($menu['out_of_stock'] ?? 0),
                'hidden' => (int)($menu['hidden'] ?? 0),
            ],
            'categories' => [
                'total' => (int)($categories['total'] ?? 0),
                'active' => (int)($categories['active'] ?? 0),
                'inactive' => (int)($categories['inactive'] ?? 0),
            ],
            'combos' => [
                'total' => (int)($combos['total'] ?? 0),
                'active' => (int)($combos['active'] ?? 0),
            ],
        ];
    }

    public function tables(string $search = '', string $status = ''): array
    {
        $sql = "
            SELECT
                t.*,
                t.table_name AS name,
                ts.id AS session_id,
                ts.combo_id,
                ts.payment_status,
                ts.status AS session_status,
                ts.total_amount,
                ts.start_time,
                ts.end_time,
                bc.combo_name,
                bc.price_per_person,
                MAX(o.id) AS current_order_id,
                COUNT(DISTINCT o.id) AS order_count,
                COUNT(oi.id) AS item_count
            FROM restaurant_tables t
            LEFT JOIN table_sessions ts
                ON ts.id = (
                    SELECT ts2.id
                    FROM table_sessions ts2
                    WHERE ts2.table_id = t.id
                    ORDER BY
                        CASE WHEN ts2.end_time IS NULL THEN 0 ELSE 1 END,
                        ts2.start_time DESC,
                        ts2.id DESC
                    LIMIT 1
                )
            LEFT JOIN buffet_combos bc
                ON bc.id = ts.combo_id
            LEFT JOIN orders o
                ON o.session_id = ts.id
            LEFT JOIN order_items oi
                ON oi.order_id = o.id
            WHERE (:search = '' OR t.table_name LIKE :keyword)
              AND (:status_filter = '' OR t.status = :status)
            GROUP BY
                t.id, t.table_code, t.table_name, t.status,
                ts.id, ts.combo_id, ts.payment_status, ts.status,
                ts.total_amount, ts.start_time, ts.end_time,
                bc.combo_name, bc.price_per_person
            ORDER BY t.table_name ASC
        ";

        return $this->fetchAll($sql, [
            'search' => $search,
            'keyword' => '%' . $search . '%',
            'status_filter' => $status,
            'status' => $status,
        ]);
    }

    public function tableDetail(int $id): ?array
    {
        return $this->fetchOne(
            "
            SELECT
                t.*,
                t.table_name AS name,
                ts.id AS session_id,
                ts.combo_id,
                ts.payment_status,
                ts.status AS session_status,
                ts.total_amount,
                ts.start_time,
                ts.end_time,
                ts.paid_guest_count,
                ts.free_child_count,
                ts.payment_method,
                bc.combo_name,
                bc.price_per_person,
                MAX(o.id) AS current_order_id,
                COUNT(DISTINCT o.id) AS order_count,
                COUNT(oi.id) AS item_count,
                SUM(oi.status IN ('pending', 'approved')) AS unfinished_count
            FROM restaurant_tables t
            LEFT JOIN table_sessions ts
                ON ts.id = (
                    SELECT ts2.id
                    FROM table_sessions ts2
                    WHERE ts2.table_id = t.id
                    ORDER BY
                        CASE WHEN ts2.end_time IS NULL THEN 0 ELSE 1 END,
                        ts2.start_time DESC,
                        ts2.id DESC
                    LIMIT 1
                )
            LEFT JOIN buffet_combos bc
                ON bc.id = ts.combo_id
            LEFT JOIN orders o
                ON o.session_id = ts.id
            LEFT JOIN order_items oi
                ON oi.order_id = o.id
            WHERE t.id = :id
            GROUP BY
                t.id, t.table_code, t.table_name, t.status,
                ts.id, ts.combo_id, ts.payment_status, ts.status,
                ts.total_amount, ts.start_time, ts.end_time,
                ts.paid_guest_count, ts.free_child_count, ts.payment_method,
                bc.combo_name, bc.price_per_person
            ",
            ['id' => $id]
        );
    }

    public function tableItems(int $tableId): array
    {
        return $this->fetchAll(
            "
            SELECT
                oi.*,
                mi.item_name AS menu_name,
                mi.image,
                o.id AS order_id,
                o.status AS order_status,
                o.created_at AS order_created_at,
                ts.id AS session_id,
                ts.payment_status,
                ts.total_amount,
                t.table_name AS table_name
            FROM restaurant_tables t
            JOIN table_sessions ts
                ON ts.table_id = t.id
            JOIN orders o
                ON o.session_id = ts.id
            JOIN order_items oi
                ON oi.order_id = o.id
            JOIN menu_items mi
                ON mi.id = oi.menu_item_id
            WHERE t.id = :table_id
              AND ts.id = (
                    SELECT ts2.id
                    FROM table_sessions ts2
                    WHERE ts2.table_id = t.id
                    ORDER BY
                        CASE WHEN ts2.end_time IS NULL THEN 0 ELSE 1 END,
                        ts2.start_time DESC,
                        ts2.id DESC
                    LIMIT 1
              )
            ORDER BY oi.created_at DESC, oi.id DESC
            ",
            ['table_id' => $tableId]
        );
    }

    public function closeTable(int $id): bool
    {
        try {
            if (!$this->db instanceof PDO) {
                return false;
            }

            // Business rule (giống app): chỉ đóng bàn khi mọi món đã được phục vụ hoặc từ chối.
            $unfinished = (int)$this->fetchColumn(
                "SELECT COUNT(*)
                 FROM order_items oi
                 JOIN orders o ON o.id = oi.order_id
                 JOIN table_sessions ts ON ts.id = o.session_id
                 WHERE ts.table_id = :id
                   AND ts.end_time IS NULL
                   AND oi.status IN ('pending', 'approved')",
                ['id' => $id]
            );

            if ($unfinished > 0) {
                $this->error = "Còn {$unfinished} món chưa phục vụ hoặc chưa từ chối. Hãy hoàn tất tất cả món trước khi đóng bàn.";
                return false;
            }

            $this->db->beginTransaction();

            $this->execute(
                "UPDATE table_sessions
                 SET status = 'closed',
                     end_time = COALESCE(end_time, NOW())
                 WHERE table_id = :id
                   AND end_time IS NULL",
                ['id' => $id]
            );

            $this->execute(
                "UPDATE restaurant_tables
                 SET status = 'available'
                 WHERE id = :id",
                ['id' => $id]
            );

            $this->db->commit();
            return true;
        } catch (Throwable $e) {
            if ($this->db instanceof PDO && $this->db->inTransaction()) {
                $this->db->rollBack();
            }

            $this->error = $e->getMessage();
            return false;
        }
    }

    public function confirmPayment(int $id): bool
    {
        try {
            if (!$this->db instanceof PDO) {
                return false;
            }

            $this->db->beginTransaction();

            $this->execute(
                "UPDATE table_sessions
                 SET payment_status = 'paid',
                     status = 'closed',
                     end_time = COALESCE(end_time, NOW())
                 WHERE table_id = :id
                   AND end_time IS NULL",
                ['id' => $id]
            );

            $this->execute(
                "UPDATE restaurant_tables
                 SET status = 'available'
                 WHERE id = :id",
                ['id' => $id]
            );

            $this->db->commit();
            return true;
        } catch (Throwable $e) {
            if ($this->db instanceof PDO && $this->db->inTransaction()) {
                $this->db->rollBack();
            }

            $this->error = $e->getMessage();
            return false;
        }
    }

    public function pendingOrderItems(): array
    {
        return $this->fetchAll(
            "
            SELECT
                oi.*,
                mi.item_name AS menu_name,
                t.table_name AS table_name,
                o.id AS order_id,
                o.status AS order_status,
                o.created_at AS order_created_at,
                ts.id AS session_id
            FROM order_items oi
            JOIN orders o
                ON o.id = oi.order_id
            JOIN table_sessions ts
                ON ts.id = o.session_id
            JOIN restaurant_tables t
                ON t.id = ts.table_id
            JOIN menu_items mi
                ON mi.id = oi.menu_item_id
            WHERE oi.status IN ('pending', 'approved')
            ORDER BY oi.created_at ASC, oi.id ASC
            "
        );
    }

    /**
     * Danh sách món lọc theo trạng thái + ngày (giống get_pending_orders.php của app).
     * $status: pending|approved|served|rejected ; $date: 'YYYY-MM-DD' hoặc '' (tất cả).
     */
    public function orderItems(string $status = 'pending', string $date = ''): array
    {
        if (!in_array($status, ['pending', 'approved', 'served', 'rejected'], true)) {
            $status = 'pending';
        }

        $sql = "
            SELECT
                oi.*,
                mi.item_name AS menu_name,
                mi.image,
                t.table_name AS table_name,
                t.table_code,
                o.id AS order_id,
                o.order_code,
                o.status AS order_status,
                o.created_at AS order_created_at,
                ts.id AS session_id
            FROM order_items oi
            JOIN orders o
                ON o.id = oi.order_id
            JOIN table_sessions ts
                ON ts.id = o.session_id
            JOIN restaurant_tables t
                ON t.id = ts.table_id
            JOIN menu_items mi
                ON mi.id = oi.menu_item_id
            WHERE oi.status = :status
        ";

        $params = ['status' => $status];

        if ($date !== '' && preg_match('/^\d{4}-\d{2}-\d{2}$/', $date) === 1) {
            $sql .= " AND DATE(oi.created_at) = :date";
            $params['date'] = $date;
        }

        $sql .= " ORDER BY o.id DESC, oi.id ASC";

        return $this->fetchAll($sql, $params);
    }

    /** Đếm số món theo từng trạng thái cho bộ lọc (tùy chọn lọc theo ngày). */
    public function orderItemCounts(string $date = ''): array
    {
        $sql = "SELECT status, COUNT(*) AS total FROM order_items oi";
        $params = [];

        if ($date !== '' && preg_match('/^\d{4}-\d{2}-\d{2}$/', $date) === 1) {
            $sql .= " WHERE DATE(oi.created_at) = :date";
            $params['date'] = $date;
        }

        $sql .= " GROUP BY status";

        $rows = $this->fetchAll($sql, $params);
        $counts = ['pending' => 0, 'approved' => 0, 'served' => 0, 'rejected' => 0];
        foreach ($rows as $row) {
            if (isset($counts[$row['status']])) {
                $counts[$row['status']] = (int)$row['total'];
            }
        }

        return $counts;
    }

    public function updateOrderItemStatus(int $id, string $status): bool
    {
        $status = $this->normalizeOrderItemStatus($status);

        if (!in_array($status, ['pending', 'approved', 'served', 'rejected'], true)) {
            $this->error = 'Invalid order item status.';
            return false;
        }

        return $this->execute(
            "UPDATE order_items
             SET status = :status,
                 updated_at = NOW()
             WHERE id = :id",
            [
                'id' => $id,
                'status' => $status,
            ]
        );
    }

    public function menuItems(string $search = '', string $status = ''): array
    {
        $sql = "
            SELECT
                mi.*,
                mi.item_name AS name,
                c.category_name,
                c.category_name AS category_display_name
            FROM menu_items mi
            LEFT JOIN categories c
                ON c.id = mi.category_id
            WHERE (:search = '' OR mi.item_name LIKE :keyword OR c.category_name LIKE :keyword2)
              AND (:status_filter = '' OR mi.status = :status)
            ORDER BY mi.id DESC
        ";

        return $this->fetchAll($sql, [
            'search' => $search,
            'keyword' => '%' . $search . '%',
            'keyword2' => '%' . $search . '%',
            'status_filter' => $status,
            'status' => $status,
        ]);
    }

    public function setMenuItemStatus(int $id, string $status): bool
    {
        if (!in_array($status, ['available', 'out_of_stock', 'hidden'], true)) {
            $this->error = 'Invalid menu item status.';
            return false;
        }

        return $this->execute(
            "UPDATE menu_items SET status = :status WHERE id = :id",
            ['id' => $id, 'status' => $status]
        );
    }

    public function createMenuItem(array $data = []): bool
    {
        $data = $this->normalizeMenuItemData($data);
        unset($data['id']);

        return $this->execute(
            "INSERT INTO menu_items (category_id, item_name, image, description, status)
             VALUES (:category_id, :item_name, :image, :description, :status)",
            $data
        );
    }

    public function updateMenuItem(array $data = []): bool
    {
        $data = $this->normalizeMenuItemData($data + ['id' => 0]);
        $data['id'] = (int)($data['id'] ?? 0);

        if ($data['id'] <= 0) {
            $this->error = 'Invalid menu item id.';
            return false;
        }

        if ($data['image'] === '') {
            unset($data['image']);

            return $this->execute(
                "UPDATE menu_items
                 SET category_id = :category_id,
                     item_name = :item_name,
                     description = :description,
                     status = :status
                 WHERE id = :id",
                $data
            );
        }

        return $this->execute(
            "UPDATE menu_items
             SET category_id = :category_id,
                 item_name = :item_name,
                 image = :image,
                 description = :description,
                 status = :status
             WHERE id = :id",
            $data
        );
    }

    public function deleteMenuItem(int $id): bool
    {
        // Soft-delete giống app: ẩn món thay vì xóa cứng (tránh vỡ ràng buộc đơn hàng cũ).
        return $this->execute(
            "UPDATE menu_items SET status = 'hidden' WHERE id = :id",
            ['id' => $id]
        );
    }

    public function categories(): array
    {
        return $this->fetchAll(
            "SELECT
                c.id,
                c.category_name,
                c.category_name AS name,
                c.status,
                (SELECT COUNT(*) FROM menu_items mi
                 WHERE mi.category_id = c.id AND mi.status <> 'hidden') AS item_count
             FROM categories c
             ORDER BY c.status ASC, c.category_name ASC"
        );
    }

    /** Chỉ danh mục đang hoạt động — dùng cho dropdown khi thêm/sửa món. */
    public function activeCategories(): array
    {
        return $this->fetchAll(
            "SELECT id, category_name, category_name AS name, status
             FROM categories
             WHERE status = 'active'
             ORDER BY category_name ASC"
        );
    }

    public function setCategoryStatus(int $id, string $status): bool
    {
        if (!in_array($status, ['active', 'inactive'], true)) {
            $this->error = 'Invalid category status.';
            return false;
        }

        return $this->execute(
            "UPDATE categories SET status = :status WHERE id = :id",
            ['id' => $id, 'status' => $status]
        );
    }

    public function createCategory(string|array $data = [], string $status = 'active'): bool
    {
        $data = $this->normalizeCategoryData($data, $status);

        return $this->execute(
            "INSERT INTO categories (category_name, status)
             VALUES (:category_name, :status)",
            $data
        );
    }

    public function updateCategory(int|array $id, ?string $name = null, string $status = 'active'): bool
    {
        if (is_array($id)) {
            $data = $this->normalizeCategoryData($id);
            $data['id'] = (int)($id['id'] ?? 0);
        } else {
            $data = $this->normalizeCategoryData(['category_name' => $name, 'status' => $status]);
            $data['id'] = $id;
        }

        if ($data['id'] <= 0) {
            $this->error = 'Invalid category id.';
            return false;
        }

        return $this->execute(
            "UPDATE categories
             SET category_name = :category_name,
                 status = :status
             WHERE id = :id",
            $data
        );
    }

    public function deleteCategory(int $id): bool
    {
        // Soft-delete giống app: chuyển sang inactive thay vì xóa cứng (tránh vỡ ràng buộc khóa ngoại).
        return $this->execute(
            "UPDATE categories SET status = 'inactive' WHERE id = :id",
            ['id' => $id]
        );
    }

    public function combos(): array
    {
        return $this->fetchAll(
            "SELECT
                bc.id,
                bc.combo_name,
                bc.combo_name AS name,
                bc.price_per_person,
                bc.price_per_person AS price,
                bc.description,
                bc.status,
                (SELECT COUNT(*) FROM combo_menu_items cmi
                 WHERE cmi.combo_id = bc.id) AS item_count
             FROM buffet_combos bc
             ORDER BY bc.status ASC, bc.id DESC"
        );
    }

    /** Danh sách menu_item_id đang gán vào combo (để tích sẵn khi sửa). */
    public function comboFoodIds(int $comboId): array
    {
        $rows = $this->fetchAll(
            "SELECT menu_item_id FROM combo_menu_items WHERE combo_id = :id",
            ['id' => $comboId]
        );

        return array_map(static fn ($r) => (int)$r['menu_item_id'], $rows);
    }

    /** Map combo_id => số món, để hiển thị nhanh trên danh sách. */
    public function comboFoodIdMap(): array
    {
        $rows = $this->fetchAll(
            "SELECT combo_id, menu_item_id FROM combo_menu_items"
        );

        $map = [];
        foreach ($rows as $r) {
            $map[(int)$r['combo_id']][] = (int)$r['menu_item_id'];
        }

        return $map;
    }

    public function createCombo(array $data = []): bool
    {
        $foodIds = $this->normalizeFoodIds($data['food_ids'] ?? []);
        $data = $this->normalizeComboData($data);
        unset($data['id']);

        try {
            if (!$this->db instanceof PDO) {
                return false;
            }

            $this->db->beginTransaction();

            $stmt = $this->db->prepare(
                "INSERT INTO buffet_combos (combo_name, price_per_person, description, status)
                 VALUES (:combo_name, :price_per_person, :description, :status)"
            );
            $stmt->execute($data);
            $comboId = (int)$this->db->lastInsertId();

            $this->syncComboFoods($comboId, $foodIds);

            $this->db->commit();
            return true;
        } catch (Throwable $e) {
            if ($this->db instanceof PDO && $this->db->inTransaction()) {
                $this->db->rollBack();
            }
            $this->error = $e->getMessage();
            return false;
        }
    }

    public function updateCombo(array $data = []): bool
    {
        $foodIds = $this->normalizeFoodIds($data['food_ids'] ?? []);
        $hasFoodIds = array_key_exists('food_ids', $data);
        $id = (int)($data['id'] ?? 0);
        $data = $this->normalizeComboData($data + ['id' => 0]);
        $data['id'] = $id;

        if ($data['id'] <= 0) {
            $this->error = 'Invalid combo id.';
            return false;
        }

        try {
            if (!$this->db instanceof PDO) {
                return false;
            }

            $this->db->beginTransaction();

            $stmt = $this->db->prepare(
                "UPDATE buffet_combos
                 SET combo_name = :combo_name,
                     price_per_person = :price_per_person,
                     description = :description,
                     status = :status
                 WHERE id = :id"
            );
            $stmt->execute($data);

            if ($hasFoodIds) {
                $this->syncComboFoods($data['id'], $foodIds);
            }

            $this->db->commit();
            return true;
        } catch (Throwable $e) {
            if ($this->db instanceof PDO && $this->db->inTransaction()) {
                $this->db->rollBack();
            }
            $this->error = $e->getMessage();
            return false;
        }
    }

    public function deleteCombo(int $id): bool
    {
        // Soft-delete giống app: chuyển combo sang inactive.
        return $this->execute(
            "UPDATE buffet_combos SET status = 'inactive' WHERE id = :id",
            ['id' => $id]
        );
    }

    /** Gán lại toàn bộ danh sách món của combo (xóa cũ, thêm mới). Gọi trong transaction. */
    private function syncComboFoods(int $comboId, array $foodIds): void
    {
        $this->db->prepare('DELETE FROM combo_menu_items WHERE combo_id = ?')->execute([$comboId]);

        if (!$foodIds) {
            return;
        }

        $insert = $this->db->prepare('INSERT INTO combo_menu_items (combo_id, menu_item_id) VALUES (?, ?)');
        foreach ($foodIds as $foodId) {
            $insert->execute([$comboId, $foodId]);
        }
    }

    private function normalizeFoodIds(mixed $foodIds): array
    {
        if (!is_array($foodIds)) {
            return [];
        }

        return array_values(array_unique(array_filter(
            array_map('intval', $foodIds),
            static fn (int $id): bool => $id > 0
        )));
    }

    private function scalar(string $sql, array $params = []): int|float
    {
        $value = $this->fetchColumn($sql, $params);
        return is_numeric($value) ? $value + 0 : 0;
    }

    private function fetchColumn(string $sql, array $params = []): mixed
    {
        try {
            if (!$this->db instanceof PDO) {
                return null;
            }

            $stmt = $this->db->prepare($sql);
            $stmt->execute($params);

            return $stmt->fetchColumn();
        } catch (Throwable $e) {
            $this->error = $e->getMessage();
            return null;
        }
    }

    private function fetchOne(string $sql, array $params = []): ?array
    {
        try {
            if (!$this->db instanceof PDO) {
                return null;
            }

            $stmt = $this->db->prepare($sql);
            $stmt->execute($params);
            $row = $stmt->fetch(PDO::FETCH_ASSOC);

            return $row ?: null;
        } catch (Throwable $e) {
            $this->error = $e->getMessage();
            return null;
        }
    }

    private function fetchAll(string $sql, array $params = []): array
    {
        try {
            if (!$this->db instanceof PDO) {
                return [];
            }

            $stmt = $this->db->prepare($sql);
            $stmt->execute($params);

            return $stmt->fetchAll(PDO::FETCH_ASSOC);
        } catch (Throwable $e) {
            $this->error = $e->getMessage();
            return [];
        }
    }

    private function execute(string $sql, array $params = []): bool
    {
        try {
            if (!$this->db instanceof PDO) {
                return false;
            }

            $stmt = $this->db->prepare($sql);
            return $stmt->execute($params);
        } catch (Throwable $e) {
            $this->error = $e->getMessage();
            return false;
        }
    }

    private function normalizeMenuItemData(array $data): array
    {
        $status = $data['status'] ?? 'available';
        $status = $status === 'unavailable' ? 'out_of_stock' : $status;

        if (!in_array($status, ['available', 'out_of_stock', 'hidden'], true)) {
            $status = 'available';
        }

        return [
            'id' => (int)($data['id'] ?? 0),
            'category_id' => (int)($data['category_id'] ?? 0),
            'item_name' => trim((string)($data['item_name'] ?? $data['name'] ?? '')),
            'image' => trim((string)($data['image'] ?? '')),
            'description' => trim((string)($data['description'] ?? '')),
            'status' => $status,
        ];
    }

    private function normalizeCategoryData(string|array $data, string $status = 'active'): array
    {
        if (is_string($data)) {
            $data = [
                'category_name' => $data,
                'status' => $status,
            ];
        }

        $status = $data['status'] ?? 'active';
        if (!in_array($status, ['active', 'inactive'], true)) {
            $status = 'active';
        }

        return [
            'category_name' => trim((string)($data['category_name'] ?? $data['name'] ?? '')),
            'status' => $status,
        ];
    }

    private function normalizeComboData(array $data): array
    {
        $status = $data['status'] ?? 'active';
        if (!in_array($status, ['active', 'inactive'], true)) {
            $status = 'active';
        }

        return [
            'id' => (int)($data['id'] ?? 0),
            'combo_name' => trim((string)($data['combo_name'] ?? $data['name'] ?? '')),
            'price_per_person' => (float)($data['price_per_person'] ?? $data['price'] ?? 0),
            'description' => trim((string)($data['description'] ?? '')),
            'status' => $status,
        ];
    }

    private function normalizeOrderItemStatus(string $status): string
    {
        return match ($status) {
            'processing' => 'approved',
            default => $status,
        };
    }
}
