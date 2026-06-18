CREATE DATABASE IF NOT EXISTS buffet_ordering
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE buffet_ordering;

CREATE TABLE IF NOT EXISTS restaurant_tables (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    capacity INT DEFAULT 4,
    status ENUM('available', 'occupied', 'reserved') NOT NULL DEFAULT 'available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_restaurant_tables_name (name)
);

CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_categories_name (name)
);

CREATE TABLE IF NOT EXISTS menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(160) NOT NULL,
    price DECIMAL(12, 2) NOT NULL DEFAULT 0,
    image VARCHAR(255) NULL,
    status ENUM('available', 'unavailable') NOT NULL DEFAULT 'available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_category FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS combos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    price DECIMAL(12, 2) NOT NULL DEFAULT 0,
    description VARCHAR(255) NULL,
    status ENUM('active', 'inactive') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_id INT NOT NULL,
    status ENUM('open', 'serving', 'closed') NOT NULL DEFAULT 'open',
    payment_status ENUM('unpaid', 'paid') NOT NULL DEFAULT 'unpaid',
    total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_table FOREIGN KEY (table_id) REFERENCES restaurant_tables(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    note VARCHAR(255) NULL,
    status ENUM('pending', 'approved', 'rejected', 'served') NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_order_item_menu FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

INSERT INTO restaurant_tables (name, capacity, status) VALUES
('A01', 4, 'occupied'),
('A02', 4, 'available'),
('A03', 6, 'reserved'),
('B01', 4, 'available'),
('B02', 8, 'occupied')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO categories (id, name) VALUES
(1, 'Khai vị'),
(2, 'Món nướng'),
(3, 'Lẩu buffet'),
(4, 'Đồ uống')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO menu_items (id, category_id, name, price, status) VALUES
(1, 2, 'Bò Mỹ sốt tiêu', 99000, 'available'),
(2, 3, 'Lẩu Thái hải sản', 159000, 'available'),
(3, 1, 'Salad rong biển', 49000, 'available'),
(4, 4, 'Nước ngọt', 25000, 'available')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO combos (id, name, price, description, status) VALUES
(1, 'Standard Buffet', 239000, 'Gói buffet trưa các ngày trong tuần', 'active'),
(2, 'Premium Buffet', 399000, 'Thêm hải sản, bò Mỹ và sashimi', 'active')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO orders (id, table_id, status, payment_status, total_amount) VALUES
(1, 1, 'open', 'unpaid', 456000),
(2, 5, 'serving', 'unpaid', 638000)
ON DUPLICATE KEY UPDATE total_amount = VALUES(total_amount);

INSERT INTO order_items (id, order_id, menu_item_id, quantity, note, status) VALUES
(1, 1, 1, 2, 'Ít cay', 'pending'),
(2, 1, 3, 1, '', 'approved'),
(3, 2, 2, 2, 'Thêm nước lẩu', 'pending')
ON DUPLICATE KEY UPDATE quantity = VALUES(quantity);
