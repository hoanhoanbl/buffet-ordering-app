-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 17, 2026 at 03:26 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `buffet_ordering`
--

-- --------------------------------------------------------

--
-- Table structure for table `buffet_combos`
--

CREATE TABLE `buffet_combos` (
  `id` int(11) NOT NULL,
  `combo_name` varchar(100) NOT NULL,
  `price_per_person` decimal(12,2) NOT NULL,
  `description` text DEFAULT NULL,
  `status` enum('active','inactive') DEFAULT 'active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `buffet_combos`
--

INSERT INTO `buffet_combos` (`id`, `combo_name`, `price_per_person`, `description`, `status`) VALUES
(1, 'Combo tự do 209', 209000.00, 'Combo buffet cơ bản gồm các món nướng, lẩu và đồ uống phổ thông.', 'active'),
(2, 'Combo Nướng & Lẩu 229', 229000.00, 'Combo buffet đầy đủ hơn, có thêm hải sản và món đặc biệt.', 'active'),
(3, 'Combo Nướng & Lẩu 299', 299000.00, 'Combo cao cấp gồm hải sản, bò Mỹ, sashimi và món tráng miệng.', 'active');

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `category_name` varchar(100) NOT NULL,
  `status` enum('active','inactive') DEFAULT 'active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `category_name`, `status`) VALUES
(1, 'Món nướng', 'active'),
(2, 'Món lẩu', 'active'),
(3, 'Hải sản', 'active'),
(4, 'Đồ uống', 'active'),
(5, 'Tráng miệng', 'active'),
(6, 'Món khai vị', 'active'),
(7, 'Món ăn kèm', 'active');

-- --------------------------------------------------------

--
-- Table structure for table `combo_menu_items`
--

CREATE TABLE `combo_menu_items` (
  `id` int(11) NOT NULL,
  `combo_id` int(11) NOT NULL,
  `menu_item_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `combo_menu_items`
--

INSERT INTO `combo_menu_items` (`id`, `combo_id`, `menu_item_id`) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 1, 3),
(4, 1, 5),
(5, 1, 6),
(6, 1, 13),
(7, 1, 14),
(8, 1, 16),
(9, 1, 17),
(10, 1, 19),
(11, 1, 21),
(12, 1, 22),
(13, 1, 24),
(14, 1, 25),
(15, 1, 26),
(16, 2, 1),
(17, 2, 2),
(18, 2, 3),
(19, 2, 4),
(20, 2, 5),
(21, 2, 6),
(22, 2, 7),
(23, 2, 9),
(24, 2, 10),
(25, 2, 11),
(26, 2, 13),
(27, 2, 14),
(28, 2, 15),
(29, 2, 16),
(30, 2, 17),
(31, 2, 18),
(32, 2, 19),
(33, 2, 20),
(34, 2, 21),
(35, 2, 22),
(36, 2, 23),
(37, 2, 24),
(38, 2, 25),
(39, 2, 26),
(40, 2, 27),
(41, 3, 1),
(42, 3, 2),
(43, 3, 3),
(44, 3, 4),
(45, 3, 5),
(46, 3, 6),
(47, 3, 7),
(48, 3, 8),
(49, 3, 9),
(50, 3, 10),
(51, 3, 11),
(52, 3, 12),
(53, 3, 13),
(54, 3, 14),
(55, 3, 15),
(56, 3, 16),
(57, 3, 17),
(58, 3, 18),
(59, 3, 19),
(60, 3, 20),
(61, 3, 21),
(62, 3, 22),
(63, 3, 23),
(64, 3, 24),
(65, 3, 25),
(66, 3, 26),
(67, 3, 27);

-- --------------------------------------------------------

--
-- Table structure for table `menu_items`
--

CREATE TABLE `menu_items` (
  `id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `item_name` varchar(150) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `status` enum('available','out_of_stock','hidden') DEFAULT 'available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `menu_items`
--

INSERT INTO `menu_items` (`id`, `category_id`, `item_name`, `image`, `description`, `status`) VALUES
(1, 5, 'Salad hoa quả', 'uploads/foods/60000023_saladhoaqua_1.jpg', 'Salad hoa quả - Tráng miệng', 'available'),
(2, 6, 'Salad cá ngừ', 'uploads/foods/60000025_saladcangu_3.jpg', 'Salad cá ngừ - Món khai vị', 'available'),
(3, 3, 'Cá mú tẩm đặc xốt chấm', 'uploads/foods/60000056_camutdacoxotcham.jpg', 'Cá mú tẩm đặc xốt chấm - Hải sản', 'available'),
(4, 7, 'Cơm bắt đá nóng', 'uploads/foods/60000096_combatdanong_alc_1.jpg', 'Cơm bắt đá nóng - Món ăn kèm', 'available'),
(5, 7, 'Cơm Hàn Quốc', 'uploads/foods/60000098_comhanquoc_1.jpg', 'Cơm Hàn Quốc - Món ăn kèm', 'available'),
(6, 7, 'Mỳ bò Hàn Quốc', 'uploads/foods/60000106_mybohanquoc.jpg', 'Mỳ bò Hàn Quốc - Món ăn kèm', 'available'),
(7, 7, 'Miến xào Hàn Quốc', 'uploads/foods/60000107_mienxaohanquoc.jpg', 'Miến xào Hàn Quốc - Món ăn kèm', 'available'),
(8, 7, 'Mỳ đen', 'uploads/foods/60000108_myden_1.jpg', 'Mỳ đen - Món ăn kèm', 'available'),
(9, 6, 'Bánh xèo hải sản', 'uploads/foods/60000109_banhxeohaisan.jpg', 'Bánh xèo hải sản - Món khai vị', 'available'),
(10, 6, 'Tokbokki xào hải sản', 'uploads/foods/60000113_tokbokkixaohaisan_1.jpg', 'Tokbokki xào hải sản - Món khai vị', 'available'),
(11, 2, 'Lẩu Bulgogi', 'uploads/foods/60000114_laubulgogi_alc_1.jpg', 'Lẩu Bulgogi - Món lẩu', 'available'),
(12, 2, 'Lẩu kim chi', 'uploads/foods/60000115_laukimchi.jpg', 'Lẩu kim chi - Món lẩu', 'available'),
(13, 2, 'Lẩu quân đội', 'uploads/foods/60000117_lauquandoi.jpg', 'Lẩu quân đội - Món lẩu', 'available'),
(14, 6, 'Set kimbap', 'uploads/foods/60000127_setkimpab_alc_1.jpg', 'Set kimbap - Món khai vị', 'available'),
(15, 1, 'Thịt heo đặc biệt', 'uploads/foods/60000138_thitheodacbiet_1.jpg', 'Thịt heo đặc biệt - Món nướng', 'available'),
(16, 6, 'Salad mùa xuân', 'uploads/foods/60000151_saladmuaxuan_1.jpg', 'Salad mùa xuân - Món khai vị', 'available'),
(17, 5, 'Kem sữa chua', 'uploads/foods/60000155_kem_sua_chua_1_1.jpg', 'Kem sữa chua - Tráng miệng', 'available'),
(18, 5, 'Caramel', 'uploads/foods/60000165_Caramel_1_1.jpg', 'Caramel - Tráng miệng', 'available'),
(19, 5, 'Panna cotta cam', 'uploads/foods/60000166_pannacottacam_1_1.jpg', 'Panna cotta cam - Tráng miệng', 'available'),
(20, 5, 'Panna cotta kiwi', 'uploads/foods/60000167_pannacottakiwi_1_1.jpg', 'Panna cotta kiwi - Tráng miệng', 'available'),
(21, 7, 'Canh sườn bò', 'uploads/foods/60001083_canhsuonbo_1.jpg', 'Canh sườn bò - Món ăn kèm', 'available'),
(22, 3, 'Bạch tuộc xốt ớt', 'uploads/foods/60002756_bachtuocxotot_alc_3.jpg', 'Bạch tuộc xốt ớt - Hải sản', 'available'),
(23, 1, 'Sườn đế vương tươi', 'uploads/foods/60004073_suondevuongtuoi_1.jpg', 'Sườn đế vương tươi - Món nướng', 'available'),
(24, 3, 'Bào ngư', 'uploads/foods/60008388_baongu.jpg', 'Bào ngư - Hải sản', 'available'),
(25, 2, 'Lẩu lòng bò', 'uploads/foods/60010485_laulongbo.jpg', 'Lẩu lòng bò - Món lẩu', 'available'),
(26, 1, 'Gầu bò mật ong', 'uploads/foods/60010611_gaubomatong_3.jpg', 'Gầu bò mật ong - Món nướng', 'available'),
(27, 1, 'Ba chỉ bò mật ong', 'uploads/foods/60010613_bachibomatong_alc_4.jpg', 'Ba chỉ bò mật ong - Món nướng', 'available');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `session_id` int(11) NOT NULL,
  `order_code` varchar(50) NOT NULL,
  `status` enum('pending','approved','served','partially_served','rejected','empty') DEFAULT 'pending',
  `note` text DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `session_id`, `order_code`, `status`, `note`, `created_at`) VALUES
(1, 3, 'ORD20260612113911839', 'served', '', '2026-06-12 16:39:11'),
(2, 3, 'ORD20260612124020217', 'served', '', '2026-06-12 17:40:20'),
(3, 3, 'ORD20260612125910481', 'served', '', '2026-06-12 17:59:10'),
(4, 3, 'ORD20260616123623786', 'served', '', '2026-06-16 17:36:23'),
(5, 3, 'ORD20260616124414130', 'served', 'codex flow test', '2026-06-16 17:44:14'),
(6, 6, 'ORD20260616160146350', 'served', '', '2026-06-16 21:01:46'),
(7, 10, 'ORD20260616213407146', 'served', '', '2026-06-16 21:34:07'),
(8, 10, 'ORD20260616222630714', 'served', '', '2026-06-16 22:26:30'),
(9, 10, 'ORD20260616225154408', 'served', '', '2026-06-16 22:51:54'),
(10, 10, 'ORD20260616225602768', 'served', '', '2026-06-16 22:56:02');

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `menu_item_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `note` text DEFAULT NULL,
  `status` enum('pending','approved','served','rejected') DEFAULT 'pending',
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`id`, `order_id`, `menu_item_id`, `quantity`, `note`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, 27, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:23'),
(2, 1, 26, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:24'),
(3, 1, 15, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:25'),
(4, 1, 11, 3, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:26'),
(5, 1, 13, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:27'),
(6, 1, 22, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:27'),
(7, 1, 18, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:28'),
(8, 2, 23, 1, '', 'served', '2026-06-12 17:40:20', '2026-06-16 21:35:28'),
(9, 2, 26, 1, '', 'served', '2026-06-12 17:40:20', '2026-06-16 21:35:29'),
(10, 2, 15, 1, '', 'served', '2026-06-12 17:40:20', '2026-06-16 21:35:29'),
(11, 3, 6, 1, '', 'served', '2026-06-12 17:59:10', '2026-06-16 21:35:29'),
(12, 4, 27, 1, '', 'served', '2026-06-16 17:36:23', '2026-06-16 21:35:29'),
(13, 4, 26, 1, 'nhiều mật ong', 'served', '2026-06-16 17:36:23', '2026-06-16 21:35:30'),
(14, 5, 1, 1, 'test duyet don', 'served', '2026-06-16 17:44:14', '2026-06-16 21:35:31'),
(15, 6, 1, 1, 'smoke', 'served', '2026-06-16 21:01:46', '2026-06-16 21:01:46'),
(16, 7, 18, 1, '', 'served', '2026-06-16 21:34:07', '2026-06-16 21:35:32'),
(17, 7, 17, 1, '', 'served', '2026-06-16 21:34:07', '2026-06-16 21:35:33'),
(18, 7, 19, 1, '', 'served', '2026-06-16 21:34:07', '2026-06-16 21:35:33'),
(19, 7, 20, 1, '', 'served', '2026-06-16 21:34:07', '2026-06-16 21:35:34'),
(20, 8, 27, 1, '', 'served', '2026-06-16 22:26:30', '2026-06-16 22:28:18'),
(21, 8, 26, 1, '', 'served', '2026-06-16 22:26:30', '2026-06-16 22:28:18'),
(22, 8, 23, 1, '', 'served', '2026-06-16 22:26:30', '2026-06-16 22:28:20'),
(23, 9, 9, 1, '', 'served', '2026-06-16 22:51:54', '2026-06-16 23:21:26'),
(24, 9, 2, 1, '', 'served', '2026-06-16 22:51:54', '2026-06-16 23:21:26'),
(25, 10, 21, 1, '', 'served', '2026-06-16 22:56:02', '2026-06-16 23:21:27'),
(26, 10, 4, 1, '', 'served', '2026-06-16 22:56:02', '2026-06-16 23:21:27'),
(27, 10, 5, 1, '', 'served', '2026-06-16 22:56:02', '2026-06-16 23:21:27');

-- --------------------------------------------------------

--
-- Table structure for table `restaurant_tables`
--

CREATE TABLE `restaurant_tables` (
  `id` int(11) NOT NULL,
  `table_code` varchar(20) NOT NULL,
  `table_name` varchar(50) NOT NULL,
  `status` enum('available','occupied') DEFAULT 'available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `restaurant_tables`
--

INSERT INTO `restaurant_tables` (`id`, `table_code`, `table_name`, `status`) VALUES
(1, 'B01', 'Bàn 1', 'available'),
(2, 'B02', 'Bàn 2', 'available'),
(3, 'B03', 'Bàn 3', 'available'),
(4, 'B04', 'Bàn 4', 'available'),
(5, 'B05', 'Bàn 5', 'available'),
(6, 'B06', 'Bàn 6', 'available'),
(7, 'B07', 'Bàn 7', 'available'),
(8, 'B08', 'Bàn 8', 'available'),
(9, 'B09', 'Bàn 9', 'available'),
(10, 'B10', 'Bàn 10', 'available');

-- --------------------------------------------------------

--
-- Table structure for table `table_sessions`
--

CREATE TABLE `table_sessions` (
  `id` int(11) NOT NULL,
  `table_id` int(11) NOT NULL,
  `combo_id` int(11) NOT NULL,
  `paid_guest_count` int(11) NOT NULL DEFAULT 0,
  `free_child_count` int(11) NOT NULL DEFAULT 0,
  `payment_method` enum('qr','cash') DEFAULT NULL,
  `payment_status` enum('unpaid','paid') DEFAULT 'unpaid',
  `paid_at` datetime DEFAULT NULL,
  `status` enum('active','expired','closed') DEFAULT 'active',
  `total_amount` decimal(12,2) DEFAULT 0.00,
  `start_time` datetime DEFAULT current_timestamp(),
  `end_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `table_sessions`
--

INSERT INTO `table_sessions` (`id`, `table_id`, `combo_id`, `paid_guest_count`, `free_child_count`, `payment_method`, `payment_status`, `paid_at`, `status`, `total_amount`, `start_time`, `end_time`) VALUES
(3, 1, 2, 3, 1, 'cash', 'paid', '2026-06-11 00:53:23', 'expired', 897000.00, '2026-06-11 00:53:23', '2026-06-11 02:33:23'),
(4, 2, 2, 5, 0, 'qr', 'paid', '2026-06-11 12:51:54', 'expired', 1495000.00, '2026-06-11 12:51:54', '2026-06-11 14:31:54'),
(5, 3, 3, 2, 0, 'cash', 'paid', '2026-06-11 15:56:34', 'expired', 798000.00, '2026-06-11 15:56:34', '2026-06-11 17:36:34'),
(6, 4, 1, 1, 0, 'cash', 'paid', '2026-06-16 21:01:46', 'closed', 199000.00, '2026-06-16 21:01:46', '2026-06-16 22:41:46'),
(7, 4, 1, 1, 0, 'qr', 'paid', '2026-06-16 21:08:23', 'closed', 199000.00, '2026-06-16 21:08:23', '2026-06-16 22:48:23'),
(8, 4, 1, 1, 0, 'cash', 'paid', '2026-06-16 21:12:45', 'closed', 199000.00, '2026-06-16 21:12:45', '2026-06-16 21:11:45'),
(9, 4, 1, 1, 0, 'cash', 'paid', '2026-06-16 21:12:59', 'closed', 199000.00, '2026-06-16 21:12:59', '2026-06-16 22:52:59'),
(10, 4, 2, 2, 0, 'qr', 'paid', '2026-06-16 21:33:47', 'expired', 598000.00, '2026-06-16 21:33:47', '2026-06-16 23:13:47');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `role` enum('admin','user') NOT NULL DEFAULT 'user',
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `phone`, `role`, `created_at`) VALUES
(1, 'admin', '123', 'Quản trị viên', '', 'admin', '2026-05-28 17:19:10'),
(3, 'hh', '$2y$10$FxintQ8uf5T1mz3FWcelhu9keLFEture2UeBEowbodT7PBLSGhf0S', 'Nhu Hoang', '0987663331', 'user', '2026-06-06 15:48:31'),
(4, 'harry', '$2y$10$8ByOdKE/6tz27vOaVFJubOWVNZ47ytP0Nrb6EjyxSWH0PH8mowfjG', 'Tat Hien', '123', 'user', '2026-06-06 16:00:34');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `buffet_combos`
--
ALTER TABLE `buffet_combos`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `combo_menu_items`
--
ALTER TABLE `combo_menu_items`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_combo_item` (`combo_id`,`menu_item_id`),
  ADD KEY `fk_combo_menu_items_menu_item` (`menu_item_id`);

--
-- Indexes for table `menu_items`
--
ALTER TABLE `menu_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_menu_items_category` (`category_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `order_code` (`order_code`),
  ADD KEY `fk_orders_session` (`session_id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_order_items_order` (`order_id`),
  ADD KEY `fk_order_items_menu_item` (`menu_item_id`);

--
-- Indexes for table `restaurant_tables`
--
ALTER TABLE `restaurant_tables`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_table_code` (`table_code`);

--
-- Indexes for table `table_sessions`
--
ALTER TABLE `table_sessions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_table_sessions_table` (`table_id`),
  ADD KEY `fk_table_sessions_combo` (`combo_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `unique_phone` (`phone`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `buffet_combos`
--
ALTER TABLE `buffet_combos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `combo_menu_items`
--
ALTER TABLE `combo_menu_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=68;

--
-- AUTO_INCREMENT for table `menu_items`
--
ALTER TABLE `menu_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT for table `restaurant_tables`
--
ALTER TABLE `restaurant_tables`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `table_sessions`
--
ALTER TABLE `table_sessions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `combo_menu_items`
--
ALTER TABLE `combo_menu_items`
  ADD CONSTRAINT `fk_combo_menu_items_combo` FOREIGN KEY (`combo_id`) REFERENCES `buffet_combos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_combo_menu_items_menu_item` FOREIGN KEY (`menu_item_id`) REFERENCES `menu_items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `menu_items`
--
ALTER TABLE `menu_items`
  ADD CONSTRAINT `fk_menu_items_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `fk_orders_session` FOREIGN KEY (`session_id`) REFERENCES `table_sessions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `fk_order_items_menu_item` FOREIGN KEY (`menu_item_id`) REFERENCES `menu_items` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `table_sessions`
--
ALTER TABLE `table_sessions`
  ADD CONSTRAINT `fk_table_sessions_combo` FOREIGN KEY (`combo_id`) REFERENCES `buffet_combos` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_table_sessions_table` FOREIGN KEY (`table_id`) REFERENCES `restaurant_tables` (`id`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
