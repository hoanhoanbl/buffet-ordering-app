/*
 Navicat Premium Dump SQL

 Source Server         : mysql-local
 Source Server Type    : MySQL
 Source Server Version : 120102 (12.1.2-MariaDB)
 Source Host           : localhost:3306
 Source Schema         : buffet_ordering

 Target Server Type    : MySQL
 Target Server Version : 120102 (12.1.2-MariaDB)
 File Encoding         : 65001

 Date: 18/06/2026 11:45:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for buffet_combos
-- ----------------------------
DROP TABLE IF EXISTS `buffet_combos`;
CREATE TABLE `buffet_combos`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `combo_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `price_per_person` decimal(12, 2) NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of buffet_combos
-- ----------------------------
INSERT INTO `buffet_combos` VALUES (1, 'Combo t? do 209', 209000.00, '', 'active');
INSERT INTO `buffet_combos` VALUES (2, 'Combo Nướng & Lẩu 229', 229000.00, 'Combo buffet đầy đủ hơn, có thêm hải sản và món đặc biệt.', 'active');
INSERT INTO `buffet_combos` VALUES (3, 'Combo Nướng & Lẩu 299', 299000.00, 'Combo cao cấp gồm hải sản, bò Mỹ, sashimi và món tráng miệng.', 'active');

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, 'Món nướng', 'active');
INSERT INTO `categories` VALUES (2, 'Món lẩu', 'active');
INSERT INTO `categories` VALUES (3, 'Hải sản', 'active');
INSERT INTO `categories` VALUES (4, 'Đồ uống', 'active');
INSERT INTO `categories` VALUES (5, 'Tráng miệng', 'active');
INSERT INTO `categories` VALUES (6, 'Món khai vị', 'active');
INSERT INTO `categories` VALUES (7, 'Món ăn kèm', 'active');

-- ----------------------------
-- Table structure for combo_menu_items
-- ----------------------------
DROP TABLE IF EXISTS `combo_menu_items`;
CREATE TABLE `combo_menu_items`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `combo_id` int NOT NULL,
  `menu_item_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_combo_item`(`combo_id` ASC, `menu_item_id` ASC) USING BTREE,
  INDEX `fk_combo_menu_items_menu_item`(`menu_item_id` ASC) USING BTREE,
  CONSTRAINT `fk_combo_menu_items_combo` FOREIGN KEY (`combo_id`) REFERENCES `buffet_combos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_combo_menu_items_menu_item` FOREIGN KEY (`menu_item_id`) REFERENCES `menu_items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 85 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of combo_menu_items
-- ----------------------------
INSERT INTO `combo_menu_items` VALUES (70, 1, 1);
INSERT INTO `combo_menu_items` VALUES (71, 1, 2);
INSERT INTO `combo_menu_items` VALUES (72, 1, 3);
INSERT INTO `combo_menu_items` VALUES (73, 1, 5);
INSERT INTO `combo_menu_items` VALUES (74, 1, 6);
INSERT INTO `combo_menu_items` VALUES (75, 1, 13);
INSERT INTO `combo_menu_items` VALUES (76, 1, 14);
INSERT INTO `combo_menu_items` VALUES (77, 1, 16);
INSERT INTO `combo_menu_items` VALUES (78, 1, 17);
INSERT INTO `combo_menu_items` VALUES (79, 1, 19);
INSERT INTO `combo_menu_items` VALUES (80, 1, 21);
INSERT INTO `combo_menu_items` VALUES (81, 1, 22);
INSERT INTO `combo_menu_items` VALUES (82, 1, 24);
INSERT INTO `combo_menu_items` VALUES (83, 1, 25);
INSERT INTO `combo_menu_items` VALUES (84, 1, 26);
INSERT INTO `combo_menu_items` VALUES (16, 2, 1);
INSERT INTO `combo_menu_items` VALUES (17, 2, 2);
INSERT INTO `combo_menu_items` VALUES (18, 2, 3);
INSERT INTO `combo_menu_items` VALUES (19, 2, 4);
INSERT INTO `combo_menu_items` VALUES (20, 2, 5);
INSERT INTO `combo_menu_items` VALUES (21, 2, 6);
INSERT INTO `combo_menu_items` VALUES (22, 2, 7);
INSERT INTO `combo_menu_items` VALUES (23, 2, 9);
INSERT INTO `combo_menu_items` VALUES (24, 2, 10);
INSERT INTO `combo_menu_items` VALUES (25, 2, 11);
INSERT INTO `combo_menu_items` VALUES (26, 2, 13);
INSERT INTO `combo_menu_items` VALUES (27, 2, 14);
INSERT INTO `combo_menu_items` VALUES (28, 2, 15);
INSERT INTO `combo_menu_items` VALUES (29, 2, 16);
INSERT INTO `combo_menu_items` VALUES (30, 2, 17);
INSERT INTO `combo_menu_items` VALUES (31, 2, 18);
INSERT INTO `combo_menu_items` VALUES (32, 2, 19);
INSERT INTO `combo_menu_items` VALUES (33, 2, 20);
INSERT INTO `combo_menu_items` VALUES (34, 2, 21);
INSERT INTO `combo_menu_items` VALUES (35, 2, 22);
INSERT INTO `combo_menu_items` VALUES (36, 2, 23);
INSERT INTO `combo_menu_items` VALUES (37, 2, 24);
INSERT INTO `combo_menu_items` VALUES (38, 2, 25);
INSERT INTO `combo_menu_items` VALUES (39, 2, 26);
INSERT INTO `combo_menu_items` VALUES (40, 2, 27);
INSERT INTO `combo_menu_items` VALUES (41, 3, 1);
INSERT INTO `combo_menu_items` VALUES (42, 3, 2);
INSERT INTO `combo_menu_items` VALUES (43, 3, 3);
INSERT INTO `combo_menu_items` VALUES (44, 3, 4);
INSERT INTO `combo_menu_items` VALUES (45, 3, 5);
INSERT INTO `combo_menu_items` VALUES (46, 3, 6);
INSERT INTO `combo_menu_items` VALUES (47, 3, 7);
INSERT INTO `combo_menu_items` VALUES (48, 3, 8);
INSERT INTO `combo_menu_items` VALUES (49, 3, 9);
INSERT INTO `combo_menu_items` VALUES (50, 3, 10);
INSERT INTO `combo_menu_items` VALUES (51, 3, 11);
INSERT INTO `combo_menu_items` VALUES (52, 3, 12);
INSERT INTO `combo_menu_items` VALUES (53, 3, 13);
INSERT INTO `combo_menu_items` VALUES (54, 3, 14);
INSERT INTO `combo_menu_items` VALUES (55, 3, 15);
INSERT INTO `combo_menu_items` VALUES (56, 3, 16);
INSERT INTO `combo_menu_items` VALUES (57, 3, 17);
INSERT INTO `combo_menu_items` VALUES (58, 3, 18);
INSERT INTO `combo_menu_items` VALUES (59, 3, 19);
INSERT INTO `combo_menu_items` VALUES (60, 3, 20);
INSERT INTO `combo_menu_items` VALUES (61, 3, 21);
INSERT INTO `combo_menu_items` VALUES (62, 3, 22);
INSERT INTO `combo_menu_items` VALUES (63, 3, 23);
INSERT INTO `combo_menu_items` VALUES (64, 3, 24);
INSERT INTO `combo_menu_items` VALUES (65, 3, 25);
INSERT INTO `combo_menu_items` VALUES (66, 3, 26);
INSERT INTO `combo_menu_items` VALUES (67, 3, 27);

-- ----------------------------
-- Table structure for menu_items
-- ----------------------------
DROP TABLE IF EXISTS `menu_items`;
CREATE TABLE `menu_items`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `item_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `status` enum('available','out_of_stock','hidden') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'available',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_menu_items_category`(`category_id` ASC) USING BTREE,
  CONSTRAINT `fk_menu_items_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of menu_items
-- ----------------------------
INSERT INTO `menu_items` VALUES (1, 5, 'Salad hoa quả', 'uploads/foods/60000023_saladhoaqua_1.jpg', 'Salad hoa quả - Tráng miệng', 'available');
INSERT INTO `menu_items` VALUES (2, 6, 'Salad cá ngừ', 'uploads/foods/60000025_saladcangu_3.jpg', 'Salad cá ngừ - Món khai vị', 'available');
INSERT INTO `menu_items` VALUES (3, 3, 'Cá mú tẩm đặc xốt chấm', 'uploads/foods/60000056_camutdacoxotcham.jpg', 'Cá mú tẩm đặc xốt chấm - Hải sản', 'available');
INSERT INTO `menu_items` VALUES (4, 7, 'Cơm bắt đá nóng', 'uploads/foods/60000096_combatdanong_alc_1.jpg', 'Cơm bắt đá nóng - Món ăn kèm', 'available');
INSERT INTO `menu_items` VALUES (5, 7, 'Cơm Hàn Quốc', 'uploads/foods/60000098_comhanquoc_1.jpg', 'Cơm Hàn Quốc - Món ăn kèm', 'available');
INSERT INTO `menu_items` VALUES (6, 7, 'Mỳ bò Hàn Quốc', 'uploads/foods/60000106_mybohanquoc.jpg', 'Mỳ bò Hàn Quốc - Món ăn kèm', 'available');
INSERT INTO `menu_items` VALUES (7, 7, 'Miến xào Hàn Quốc', 'uploads/foods/60000107_mienxaohanquoc.jpg', 'Miến xào Hàn Quốc - Món ăn kèm', 'available');
INSERT INTO `menu_items` VALUES (8, 7, 'Mỳ đen', 'uploads/foods/60000108_myden_1.jpg', 'Mỳ đen - Món ăn kèm', 'available');
INSERT INTO `menu_items` VALUES (9, 6, 'Bánh xèo hải sản', 'uploads/foods/60000109_banhxeohaisan.jpg', 'Bánh xèo hải sản - Món khai vị', 'available');
INSERT INTO `menu_items` VALUES (10, 6, 'Tokbokki xào hải sản', 'uploads/foods/60000113_tokbokkixaohaisan_1.jpg', 'Tokbokki xào hải sản - Món khai vị', 'available');
INSERT INTO `menu_items` VALUES (11, 2, 'Lẩu Bulgogi', 'uploads/foods/60000114_laubulgogi_alc_1.jpg', 'Lẩu Bulgogi - Món lẩu', 'available');
INSERT INTO `menu_items` VALUES (12, 2, 'Lẩu kim chi', 'uploads/foods/60000115_laukimchi.jpg', 'Lẩu kim chi - Món lẩu', 'available');
INSERT INTO `menu_items` VALUES (13, 2, 'Lẩu quân đội', 'uploads/foods/60000117_lauquandoi.jpg', 'Lẩu quân đội - Món lẩu', 'available');
INSERT INTO `menu_items` VALUES (14, 6, 'Set kimbap', 'uploads/foods/60000127_setkimpab_alc_1.jpg', 'Set kimbap - Món khai vị', 'available');
INSERT INTO `menu_items` VALUES (15, 1, 'Thịt heo đặc biệt', 'uploads/foods/60000138_thitheodacbiet_1.jpg', 'Thịt heo đặc biệt - Món nướng', 'available');
INSERT INTO `menu_items` VALUES (16, 6, 'Salad mùa xuân', 'uploads/foods/60000151_saladmuaxuan_1.jpg', 'Salad mùa xuân - Món khai vị', 'available');
INSERT INTO `menu_items` VALUES (17, 5, 'Kem sữa chua', 'uploads/foods/60000155_kem_sua_chua_1_1.jpg', 'Kem sữa chua - Tráng miệng', 'available');
INSERT INTO `menu_items` VALUES (18, 5, 'Caramel', 'uploads/foods/60000165_Caramel_1_1.jpg', 'Caramel - Tráng miệng', 'available');
INSERT INTO `menu_items` VALUES (19, 5, 'Panna cotta cam', 'uploads/foods/60000166_pannacottacam_1_1.jpg', 'Panna cotta cam - Tráng miệng', 'available');
INSERT INTO `menu_items` VALUES (20, 5, 'Panna cotta kiwi', 'uploads/foods/60000167_pannacottakiwi_1_1.jpg', 'Panna cotta kiwi - Tráng miệng', 'available');
INSERT INTO `menu_items` VALUES (21, 7, 'Canh sườn bò', 'uploads/foods/60001083_canhsuonbo_1.jpg', 'Canh sườn bò - Món ăn kèm', 'available');
INSERT INTO `menu_items` VALUES (22, 3, 'Bạch tuộc xốt ớt', 'uploads/foods/60002756_bachtuocxotot_alc_3.jpg', 'Bạch tuộc xốt ớt - Hải sản 2', 'available');
INSERT INTO `menu_items` VALUES (23, 1, 'Sườn đế vương tươi', 'uploads/foods/60004073_suondevuongtuoi_1.jpg', 'Sườn đế vương tươi - Món nướng', 'available');
INSERT INTO `menu_items` VALUES (24, 3, 'Bào ngư', 'uploads/foods/60008388_baongu.jpg', 'Bào ngư - Hải sản', 'available');
INSERT INTO `menu_items` VALUES (25, 2, 'Lẩu lòng bò', 'uploads/foods/60010485_laulongbo.jpg', 'Lẩu lòng bò - Món lẩu', 'available');
INSERT INTO `menu_items` VALUES (26, 1, 'Gầu bò mật ong', 'uploads/foods/60010611_gaubomatong_3.jpg', 'Gầu bò mật ong - Món nướng', 'available');
INSERT INTO `menu_items` VALUES (27, 1, 'Ba chỉ bò mật ong', 'uploads/foods/60010613_bachibomatong_alc_4.jpg', 'Ba chỉ bò mật ong - Món nướng', 'available');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `menu_item_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `status` enum('pending','approved','served','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending',
  `created_at` datetime NULL DEFAULT current_timestamp(),
  `updated_at` datetime NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_order_items_order`(`order_id` ASC) USING BTREE,
  INDEX `fk_order_items_menu_item`(`menu_item_id` ASC) USING BTREE,
  CONSTRAINT `fk_order_items_menu_item` FOREIGN KEY (`menu_item_id`) REFERENCES `menu_items` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (1, 1, 27, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:23');
INSERT INTO `order_items` VALUES (2, 1, 26, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:24');
INSERT INTO `order_items` VALUES (3, 1, 15, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:25');
INSERT INTO `order_items` VALUES (4, 1, 11, 3, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:26');
INSERT INTO `order_items` VALUES (5, 1, 13, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:27');
INSERT INTO `order_items` VALUES (6, 1, 22, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:27');
INSERT INTO `order_items` VALUES (7, 1, 18, 1, '', 'served', '2026-06-12 16:39:11', '2026-06-16 21:35:28');
INSERT INTO `order_items` VALUES (8, 2, 23, 1, '', 'served', '2026-06-12 17:40:20', '2026-06-16 21:35:28');
INSERT INTO `order_items` VALUES (9, 2, 26, 1, '', 'served', '2026-06-12 17:40:20', '2026-06-16 21:35:29');
INSERT INTO `order_items` VALUES (10, 2, 15, 1, '', 'served', '2026-06-12 17:40:20', '2026-06-16 21:35:29');
INSERT INTO `order_items` VALUES (11, 3, 6, 1, '', 'served', '2026-06-12 17:59:10', '2026-06-16 21:35:29');
INSERT INTO `order_items` VALUES (12, 4, 27, 1, '', 'served', '2026-06-16 17:36:23', '2026-06-16 21:35:29');
INSERT INTO `order_items` VALUES (13, 4, 26, 1, 'nhiều mật ong', 'served', '2026-06-16 17:36:23', '2026-06-16 21:35:30');
INSERT INTO `order_items` VALUES (14, 5, 1, 1, 'test duyet don', 'served', '2026-06-16 17:44:14', '2026-06-16 21:35:31');
INSERT INTO `order_items` VALUES (15, 6, 1, 1, 'smoke', 'served', '2026-06-16 21:01:46', '2026-06-16 21:01:46');
INSERT INTO `order_items` VALUES (16, 7, 18, 1, '', 'served', '2026-06-16 21:34:07', '2026-06-16 21:35:32');
INSERT INTO `order_items` VALUES (17, 7, 17, 1, '', 'served', '2026-06-16 21:34:07', '2026-06-16 21:35:33');
INSERT INTO `order_items` VALUES (18, 7, 19, 1, '', 'served', '2026-06-16 21:34:07', '2026-06-16 21:35:33');
INSERT INTO `order_items` VALUES (19, 7, 20, 1, '', 'served', '2026-06-16 21:34:07', '2026-06-16 21:35:34');
INSERT INTO `order_items` VALUES (20, 8, 27, 1, '', 'served', '2026-06-16 22:26:30', '2026-06-16 22:28:18');
INSERT INTO `order_items` VALUES (21, 8, 26, 1, '', 'served', '2026-06-16 22:26:30', '2026-06-16 22:28:18');
INSERT INTO `order_items` VALUES (22, 8, 23, 1, '', 'served', '2026-06-16 22:26:30', '2026-06-16 22:28:20');
INSERT INTO `order_items` VALUES (23, 9, 9, 1, '', 'served', '2026-06-16 22:51:54', '2026-06-16 23:21:26');
INSERT INTO `order_items` VALUES (24, 9, 2, 1, '', 'served', '2026-06-16 22:51:54', '2026-06-16 23:21:26');
INSERT INTO `order_items` VALUES (25, 10, 21, 1, '', 'served', '2026-06-16 22:56:02', '2026-06-16 23:21:27');
INSERT INTO `order_items` VALUES (26, 10, 4, 1, '', 'served', '2026-06-16 22:56:02', '2026-06-16 23:21:27');
INSERT INTO `order_items` VALUES (27, 10, 5, 1, '', 'served', '2026-06-16 22:56:02', '2026-06-16 23:21:27');
INSERT INTO `order_items` VALUES (28, 11, 24, 1, '', 'served', '2026-06-17 21:10:40', '2026-06-17 22:22:32');
INSERT INTO `order_items` VALUES (29, 11, 3, 1, '', 'served', '2026-06-17 21:10:40', '2026-06-17 22:22:32');
INSERT INTO `order_items` VALUES (30, 12, 24, 1, '', 'served', '2026-06-17 22:10:35', '2026-06-18 00:24:46');
INSERT INTO `order_items` VALUES (31, 12, 3, 1, '', 'served', '2026-06-17 22:10:35', '2026-06-18 00:24:46');
INSERT INTO `order_items` VALUES (32, 12, 17, 1, '', 'served', '2026-06-17 22:10:35', '2026-06-18 00:24:46');
INSERT INTO `order_items` VALUES (33, 13, 26, 1, '', 'rejected', '2026-06-17 22:16:55', '2026-06-17 22:21:47');
INSERT INTO `order_items` VALUES (34, 13, 25, 1, '', 'rejected', '2026-06-17 22:16:55', '2026-06-17 22:21:48');
INSERT INTO `order_items` VALUES (35, 14, 26, 1, '', 'served', '2026-06-17 22:18:19', '2026-06-18 00:24:45');
INSERT INTO `order_items` VALUES (36, 15, 26, 1, '', 'served', '2026-06-17 23:17:12', '2026-06-18 00:24:49');
INSERT INTO `order_items` VALUES (37, 15, 27, 1, '', 'served', '2026-06-17 23:17:12', '2026-06-18 00:24:49');
INSERT INTO `order_items` VALUES (38, 15, 23, 1, '', 'served', '2026-06-17 23:17:12', '2026-06-18 00:24:49');
INSERT INTO `order_items` VALUES (39, 16, 26, 1, '', 'served', '2026-06-17 23:20:48', '2026-06-18 00:24:44');
INSERT INTO `order_items` VALUES (40, 16, 25, 1, '', 'served', '2026-06-17 23:20:48', '2026-06-18 00:24:44');
INSERT INTO `order_items` VALUES (41, 17, 27, 1, '', 'served', '2026-06-18 00:27:49', '2026-06-18 00:28:32');
INSERT INTO `order_items` VALUES (42, 17, 26, 1, '', 'served', '2026-06-18 00:27:49', '2026-06-18 00:28:32');
INSERT INTO `order_items` VALUES (43, 18, 27, 1, '', 'rejected', '2026-06-18 00:42:46', '2026-06-18 00:43:29');
INSERT INTO `order_items` VALUES (44, 18, 26, 1, '', 'served', '2026-06-18 00:42:46', '2026-06-18 00:50:23');
INSERT INTO `order_items` VALUES (45, 18, 23, 1, '', 'served', '2026-06-18 00:42:46', '2026-06-18 00:50:23');
INSERT INTO `order_items` VALUES (46, 19, 27, 1, '', 'rejected', '2026-06-18 00:49:42', '2026-06-18 00:51:07');
INSERT INTO `order_items` VALUES (47, 19, 26, 1, '', 'approved', '2026-06-18 00:49:42', '2026-06-18 00:51:11');
INSERT INTO `order_items` VALUES (48, 19, 12, 1, '', 'approved', '2026-06-18 00:49:42', '2026-06-18 00:51:11');
INSERT INTO `order_items` VALUES (49, 19, 21, 1, '', 'approved', '2026-06-18 00:49:42', '2026-06-18 00:51:11');
INSERT INTO `order_items` VALUES (50, 20, 26, 1, '', 'pending', '2026-06-18 02:21:12', '2026-06-18 03:04:28');
INSERT INTO `order_items` VALUES (51, 20, 22, 1, '', 'pending', '2026-06-18 02:21:12', '2026-06-18 02:21:12');
INSERT INTO `order_items` VALUES (52, 20, 6, 1, '', 'pending', '2026-06-18 02:21:12', '2026-06-18 02:21:12');
INSERT INTO `order_items` VALUES (53, 20, 1, 1, '', 'pending', '2026-06-18 02:21:12', '2026-06-18 02:21:12');
INSERT INTO `order_items` VALUES (54, 20, 2, 1, '', 'pending', '2026-06-18 02:21:12', '2026-06-18 02:21:12');
INSERT INTO `order_items` VALUES (55, 20, 13, 1, '', 'pending', '2026-06-18 02:21:12', '2026-06-18 02:21:12');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `session_id` int NOT NULL,
  `order_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('pending','approved','served','partially_served','rejected','empty') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending',
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` datetime NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_code`(`order_code` ASC) USING BTREE,
  INDEX `fk_orders_session`(`session_id` ASC) USING BTREE,
  CONSTRAINT `fk_orders_session` FOREIGN KEY (`session_id`) REFERENCES `table_sessions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 3, 'ORD20260612113911839', 'served', '', '2026-06-12 16:39:11');
INSERT INTO `orders` VALUES (2, 3, 'ORD20260612124020217', 'served', '', '2026-06-12 17:40:20');
INSERT INTO `orders` VALUES (3, 3, 'ORD20260612125910481', 'served', '', '2026-06-12 17:59:10');
INSERT INTO `orders` VALUES (4, 3, 'ORD20260616123623786', 'served', '', '2026-06-16 17:36:23');
INSERT INTO `orders` VALUES (5, 3, 'ORD20260616124414130', 'served', 'codex flow test', '2026-06-16 17:44:14');
INSERT INTO `orders` VALUES (6, 6, 'ORD20260616160146350', 'served', '', '2026-06-16 21:01:46');
INSERT INTO `orders` VALUES (7, 10, 'ORD20260616213407146', 'served', '', '2026-06-16 21:34:07');
INSERT INTO `orders` VALUES (8, 10, 'ORD20260616222630714', 'served', '', '2026-06-16 22:26:30');
INSERT INTO `orders` VALUES (9, 10, 'ORD20260616225154408', 'served', '', '2026-06-16 22:51:54');
INSERT INTO `orders` VALUES (10, 10, 'ORD20260616225602768', 'served', '', '2026-06-16 22:56:02');
INSERT INTO `orders` VALUES (11, 11, 'ORD20260617211040711', 'served', '', '2026-06-17 21:10:40');
INSERT INTO `orders` VALUES (12, 13, 'ORD20260617221035939', 'served', '', '2026-06-17 22:10:35');
INSERT INTO `orders` VALUES (13, 15, 'ORD20260617221655325', 'rejected', '', '2026-06-17 22:16:55');
INSERT INTO `orders` VALUES (14, 15, 'ORD20260617221819445', 'served', '', '2026-06-17 22:18:19');
INSERT INTO `orders` VALUES (15, 20, 'ORD20260617231712652', 'served', '', '2026-06-17 23:17:12');
INSERT INTO `orders` VALUES (16, 21, 'ORD20260617232048942', 'served', '', '2026-06-17 23:20:48');
INSERT INTO `orders` VALUES (17, 23, 'ORD20260618002749436', 'served', '', '2026-06-18 00:27:49');
INSERT INTO `orders` VALUES (18, 23, 'ORD20260618004246343', 'partially_served', '', '2026-06-18 00:42:46');
INSERT INTO `orders` VALUES (19, 24, 'ORD20260618004942397', 'approved', '', '2026-06-18 00:49:42');
INSERT INTO `orders` VALUES (20, 25, 'ORD20260618022112954', 'pending', '', '2026-06-18 02:21:12');

-- ----------------------------
-- Table structure for payments
-- ----------------------------
DROP TABLE IF EXISTS `payments`;
CREATE TABLE `payments`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `session_id` int NOT NULL,
  `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NOT NULL,
  `amount` decimal(12, 2) NOT NULL,
  `method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci NOT NULL DEFAULT 'paid',
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `transaction_id`(`transaction_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_uca1400_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payments
-- ----------------------------
INSERT INTO `payments` VALUES (3, 12, 'SIM17817080405581', 209000.00, 'qr', 'paid', '2026-06-17 21:54:00');
INSERT INTO `payments` VALUES (4, 13, 'SIM17817088064766', 209000.00, 'qr', 'paid', '2026-06-17 22:06:46');
INSERT INTO `payments` VALUES (5, 15, 'SIM17817094038734', 209000.00, 'qr', 'paid', '2026-06-17 22:16:43');
INSERT INTO `payments` VALUES (6, 18, 'SIM17817114419423', 418000.00, 'qr', 'paid', '2026-06-17 22:50:41');
INSERT INTO `payments` VALUES (7, 19, 'SIM17817127751109', 209000.00, 'qr', 'paid', '2026-06-17 23:12:55');
INSERT INTO `payments` VALUES (8, 20, 'SIM17817130183959', 229000.00, 'qr', 'paid', '2026-06-17 23:16:58');
INSERT INTO `payments` VALUES (9, 21, 'SIM17817131915849', 209000.00, 'qr', 'paid', '2026-06-17 23:19:51');
INSERT INTO `payments` VALUES (10, 22, 'SIM17817156696760', 299000.00, 'qr', 'paid', '2026-06-18 00:01:09');
INSERT INTO `payments` VALUES (11, 23, 'SIM17817172514246', 229000.00, 'qr', 'paid', '2026-06-18 00:27:31');
INSERT INTO `payments` VALUES (12, 24, 'SIM17817185509666', 598000.00, 'qr', 'paid', '2026-06-18 00:49:10');

-- ----------------------------
-- Table structure for restaurant_tables
-- ----------------------------
DROP TABLE IF EXISTS `restaurant_tables`;
CREATE TABLE `restaurant_tables`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `table_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `table_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('available','occupied') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'available',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_table_code`(`table_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of restaurant_tables
-- ----------------------------
INSERT INTO `restaurant_tables` VALUES (1, 'B01', 'Bàn 1', 'occupied');
INSERT INTO `restaurant_tables` VALUES (2, 'B02', 'Bàn 2', 'available');
INSERT INTO `restaurant_tables` VALUES (3, 'B03', 'Bàn 3', 'available');
INSERT INTO `restaurant_tables` VALUES (4, 'B04', 'Bàn 4', 'available');
INSERT INTO `restaurant_tables` VALUES (5, 'B05', 'Bàn 5', 'available');
INSERT INTO `restaurant_tables` VALUES (6, 'B06', 'Bàn 6', 'available');
INSERT INTO `restaurant_tables` VALUES (7, 'B07', 'Bàn 7', 'available');
INSERT INTO `restaurant_tables` VALUES (8, 'B08', 'Bàn 8', 'available');
INSERT INTO `restaurant_tables` VALUES (9, 'B09', 'Bàn 9', 'available');
INSERT INTO `restaurant_tables` VALUES (10, 'B10', 'Bàn 10', 'available');

-- ----------------------------
-- Table structure for table_sessions
-- ----------------------------
DROP TABLE IF EXISTS `table_sessions`;
CREATE TABLE `table_sessions`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `table_id` int NOT NULL,
  `user_id` int NULL DEFAULT NULL,
  `combo_id` int NOT NULL,
  `paid_guest_count` int NOT NULL DEFAULT 0,
  `free_child_count` int NOT NULL DEFAULT 0,
  `payment_method` enum('qr','cash') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `payment_status` enum('unpaid','paid') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'unpaid',
  `paid_at` datetime NULL DEFAULT NULL,
  `status` enum('active','expired','closed') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active',
  `total_amount` decimal(12, 2) NULL DEFAULT 0.00,
  `start_time` datetime NULL DEFAULT current_timestamp(),
  `end_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_table_sessions_table`(`table_id` ASC) USING BTREE,
  INDEX `fk_table_sessions_combo`(`combo_id` ASC) USING BTREE,
  INDEX `idx_user_status`(`user_id` ASC, `status` ASC) USING BTREE,
  CONSTRAINT `fk_table_sessions_combo` FOREIGN KEY (`combo_id`) REFERENCES `buffet_combos` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_table_sessions_table` FOREIGN KEY (`table_id`) REFERENCES `restaurant_tables` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of table_sessions
-- ----------------------------
INSERT INTO `table_sessions` VALUES (3, 1, NULL, 2, 3, 1, 'cash', 'paid', '2026-06-11 00:53:23', 'expired', 897000.00, '2026-06-11 00:53:23', '2026-06-11 02:33:23');
INSERT INTO `table_sessions` VALUES (4, 2, NULL, 2, 5, 0, 'qr', 'paid', '2026-06-11 12:51:54', 'expired', 1495000.00, '2026-06-11 12:51:54', '2026-06-11 14:31:54');
INSERT INTO `table_sessions` VALUES (5, 3, NULL, 3, 2, 0, 'cash', 'paid', '2026-06-11 15:56:34', 'expired', 798000.00, '2026-06-11 15:56:34', '2026-06-11 17:36:34');
INSERT INTO `table_sessions` VALUES (6, 4, NULL, 1, 1, 0, 'cash', 'paid', '2026-06-16 21:01:46', 'closed', 199000.00, '2026-06-16 21:01:46', '2026-06-16 22:41:46');
INSERT INTO `table_sessions` VALUES (7, 4, NULL, 1, 1, 0, 'qr', 'paid', '2026-06-16 21:08:23', 'closed', 199000.00, '2026-06-16 21:08:23', '2026-06-16 22:48:23');
INSERT INTO `table_sessions` VALUES (8, 4, NULL, 1, 1, 0, 'cash', 'paid', '2026-06-16 21:12:45', 'closed', 199000.00, '2026-06-16 21:12:45', '2026-06-16 21:11:45');
INSERT INTO `table_sessions` VALUES (9, 4, NULL, 1, 1, 0, 'cash', 'paid', '2026-06-16 21:12:59', 'closed', 199000.00, '2026-06-16 21:12:59', '2026-06-16 22:52:59');
INSERT INTO `table_sessions` VALUES (10, 4, NULL, 2, 2, 0, 'qr', 'paid', '2026-06-16 21:33:47', 'expired', 598000.00, '2026-06-16 21:33:47', '2026-06-16 23:13:47');
INSERT INTO `table_sessions` VALUES (11, 1, NULL, 1, 1, 0, 'cash', 'paid', '2026-06-17 21:10:28', 'closed', 209000.00, '2026-06-17 21:10:28', '2026-06-17 22:50:28');
INSERT INTO `table_sessions` VALUES (12, 2, NULL, 1, 1, 0, 'qr', 'paid', '2026-06-17 21:54:00', 'closed', 209000.00, '2026-06-17 21:53:52', '2026-06-17 23:33:52');
INSERT INTO `table_sessions` VALUES (13, 1, NULL, 1, 1, 0, 'qr', 'paid', '2026-06-17 22:06:46', 'closed', 209000.00, '2026-06-17 22:06:31', '2026-06-17 23:46:31');
INSERT INTO `table_sessions` VALUES (14, 1, NULL, 1, 1, 0, 'qr', 'unpaid', NULL, 'closed', 209000.00, '2026-06-17 22:14:58', '2026-06-17 23:54:58');
INSERT INTO `table_sessions` VALUES (15, 2, NULL, 1, 1, 0, 'qr', 'paid', '2026-06-17 22:16:43', 'closed', 209000.00, '2026-06-17 22:16:30', '2026-06-17 23:56:30');
INSERT INTO `table_sessions` VALUES (18, 1, 3, 1, 2, 0, 'qr', 'paid', '2026-06-17 22:50:41', 'closed', 418000.00, '2026-06-17 22:50:38', '2026-06-18 00:30:38');
INSERT INTO `table_sessions` VALUES (19, 1, 3, 1, 1, 0, 'qr', 'paid', '2026-06-17 23:12:55', 'closed', 209000.00, '2026-06-17 23:07:42', '2026-06-18 00:47:42');
INSERT INTO `table_sessions` VALUES (20, 2, 3, 2, 1, 0, 'qr', 'paid', '2026-06-17 23:16:58', 'closed', 229000.00, '2026-06-17 23:15:57', '2026-06-18 00:55:57');
INSERT INTO `table_sessions` VALUES (21, 1, 3, 1, 1, 0, 'qr', 'paid', '2026-06-17 23:19:51', 'closed', 209000.00, '2026-06-17 23:19:48', '2026-06-18 00:59:48');
INSERT INTO `table_sessions` VALUES (22, 2, 5, 3, 1, 0, 'qr', 'paid', '2026-06-18 00:01:09', 'closed', 299000.00, '2026-06-18 00:00:49', '2026-06-18 01:40:49');
INSERT INTO `table_sessions` VALUES (23, 1, 3, 2, 1, 0, 'qr', 'paid', '2026-06-18 00:27:31', 'closed', 229000.00, '2026-06-18 00:27:24', '2026-06-18 02:07:24');
INSERT INTO `table_sessions` VALUES (24, 2, 5, 3, 2, 1, 'qr', 'paid', '2026-06-18 00:49:10', 'expired', 598000.00, '2026-06-18 00:48:49', '2026-06-18 02:28:49');
INSERT INTO `table_sessions` VALUES (25, 1, 3, 1, 2, 0, 'cash', 'paid', '2026-06-18 02:20:18', 'active', 418000.00, '2026-06-18 02:20:18', '2026-06-18 04:00:18');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('admin','user') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user',
  `created_at` datetime NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `unique_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '123', 'Quản trị viên', '', 'admin', '2026-05-28 17:19:10');
INSERT INTO `users` VALUES (3, 'hh', '$2y$10$FxintQ8uf5T1mz3FWcelhu9keLFEture2UeBEowbodT7PBLSGhf0S', 'Nhu Hoang', '0987663331', 'user', '2026-06-06 15:48:31');
INSERT INTO `users` VALUES (4, 'harry', '$2y$10$8ByOdKE/6tz27vOaVFJubOWVNZ47ytP0Nrb6EjyxSWH0PH8mowfjG', 'Tat Hien', '123', 'user', '2026-06-06 16:00:34');
INSERT INTO `users` VALUES (5, 'tathienbadao', '$2y$10$aHO/AX7cNJq5AC/NSW0O..sVccX9DvlWQ60ZG30Ey17yLy96fZT9u', 'Harry Nguyen', '0901822630', 'user', '2026-06-17 23:59:42');

SET FOREIGN_KEY_CHECKS = 1;
