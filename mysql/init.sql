-- 洗衣店管理系统数据库初始化脚本
-- Laundry Management System Database Initialization

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 用户表 (管理员、顾客、店家)
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `role` varchar(20) NOT NULL DEFAULT 'customer' COMMENT '角色: admin-管理员 customer-顾客 shop_owner-店家',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `status` tinyint DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 店铺信息表
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '店铺ID',
  `owner_id` bigint NOT NULL COMMENT '店家ID',
  `name` varchar(100) NOT NULL COMMENT '店铺名称',
  `logo` varchar(255) DEFAULT NULL COMMENT '店铺Logo',
  `description` text COMMENT '店铺描述',
  `address` varchar(255) DEFAULT NULL COMMENT '店铺地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `business_hours` varchar(100) DEFAULT NULL COMMENT '营业时间',
  `rating` decimal(2,1) DEFAULT 5.0 COMMENT '评分',
  `status` tinyint DEFAULT 1 COMMENT '状态 0-关闭 1-营业中',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺信息表';

-- ----------------------------
-- 衣服类型表
-- ----------------------------
DROP TABLE IF EXISTS `clothes_type`;
CREATE TABLE `clothes_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `shop_id` bigint DEFAULT NULL COMMENT '店铺ID（NULL表示通用类型）',
  `name` varchar(50) NOT NULL COMMENT '类型名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `price` decimal(10,2) DEFAULT 0.00 COMMENT '基础价格',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `sort` int DEFAULT 0 COMMENT '排序',
  `status` tinyint DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='衣服类型表';

-- ----------------------------
-- 洗衣信息表
-- ----------------------------
DROP TABLE IF EXISTS `laundry_info`;
CREATE TABLE `laundry_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '洗衣信息ID',
  `shop_id` bigint NOT NULL COMMENT '店铺ID',
  `clothes_type_id` bigint NOT NULL COMMENT '衣服类型ID',
  `name` varchar(100) NOT NULL COMMENT '洗衣服务名称',
  `image` varchar(255) DEFAULT NULL COMMENT '图片',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `description` text COMMENT '服务描述',
  `wash_method` varchar(100) DEFAULT NULL COMMENT '洗涤方式',
  `delivery_time` varchar(50) DEFAULT NULL COMMENT '预计交付时间',
  `sort` int DEFAULT 0 COMMENT '排序',
  `status` tinyint DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_clothes_type_id` (`clothes_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='洗衣信息表';

-- ----------------------------
-- 订单信息表
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `customer_id` bigint NOT NULL COMMENT '顾客ID',
  `shop_id` bigint NOT NULL COMMENT '店铺ID',
  `laundry_info_id` bigint NOT NULL COMMENT '洗衣信息ID',
  `quantity` int DEFAULT 1 COMMENT '数量',
  `total_price` decimal(10,2) NOT NULL COMMENT '总价',
  `status` tinyint DEFAULT 0 COMMENT '订单状态 0-待支付 1-已支付 2-洗涤中 3-已完成 4-已取消',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `pickup_address` varchar(255) DEFAULT NULL COMMENT '取件地址',
  `delivery_address` varchar(255) DEFAULT NULL COMMENT '送达地址',
  `pickup_time` datetime DEFAULT NULL COMMENT '取件时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '送达时间',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单信息表';

-- ----------------------------
-- 订单进度表
-- ----------------------------
DROP TABLE IF EXISTS `order_progress`;
CREATE TABLE `order_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `status` tinyint NOT NULL COMMENT '状态 0-已下单 1-已取件 2-洗涤中 3-已烘干 4-已整理 5-配送中 6-已送达',
  `description` varchar(255) DEFAULT NULL COMMENT '进度描述',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单进度表';

-- ----------------------------
-- 交流区表
-- ----------------------------
DROP TABLE IF EXISTS `discussion`;
CREATE TABLE `discussion` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '图片（JSON数组）',
  `view_count` int DEFAULT 0 COMMENT '浏览量',
  `like_count` int DEFAULT 0 COMMENT '点赞数',
  `comment_count` int DEFAULT 0 COMMENT '评论数',
  `status` tinyint DEFAULT 1 COMMENT '状态 0-隐藏 1-显示',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交流区表';

-- ----------------------------
-- 评论表
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `discussion_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID',
  `content` text NOT NULL COMMENT '评论内容',
  `like_count` int DEFAULT 0 COMMENT '点赞数',
  `status` tinyint DEFAULT 1 COMMENT '状态 0-隐藏 1-显示',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_discussion_id` (`discussion_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ----------------------------
-- 系统公告表
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `type` tinyint DEFAULT 0 COMMENT '类型 0-普通公告 1-紧急公告',
  `status` tinyint DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统公告表';

-- ----------------------------
-- 在线客服消息表
-- ----------------------------
DROP TABLE IF EXISTS `customer_service`;
CREATE TABLE `customer_service` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `admin_id` bigint DEFAULT NULL COMMENT '客服ID',
  `content` text NOT NULL COMMENT '消息内容',
  `type` tinyint DEFAULT 0 COMMENT '消息类型 0-用户发送 1-客服回复',
  `is_read` tinyint DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='在线客服消息表';

-- ----------------------------
-- 初始化数据 - 管理员账号
-- ----------------------------
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '123456', '系统管理员', 'admin', 1),
('customer1', '123456', '张三', 'customer', 1),
('customer2', '123456', '李四', 'customer', 1),
('shop1', '123456', '洁净洗衣店', 'shop_owner', 1),
('shop2', '123456', '阳光洗衣坊', 'shop_owner', 1);

-- ----------------------------
-- 初始化数据 - 店铺信息
-- ----------------------------
INSERT INTO `shop` (`owner_id`, `name`, `logo`, `description`, `address`, `phone`, `business_hours`, `rating`, `status`) VALUES
(4, '洁净洗衣店', '/images/shop1.jpg', '专业洗衣服务，品质保证，让您的衣物焕然一新', '北京市朝阳区建国路88号', '010-12345678', '08:00-22:00', 4.8, 1),
(5, '阳光洗衣坊', '/images/shop2.jpg', '绿色环保洗涤，呵护您的每一件衣物', '北京市海淀区中关村大街66号', '010-87654321', '09:00-21:00', 4.6, 1);

-- ----------------------------
-- 初始化数据 - 衣服类型
-- ----------------------------
INSERT INTO `clothes_type` (`shop_id`, `name`, `price`, `description`, `sort`, `status`) VALUES
(NULL, '上衣', 15.00, '包括T恤、衬衫、毛衣等', 1, 1),
(NULL, '裤子', 18.00, '包括牛仔裤、休闲裤、西裤等', 2, 1),
(NULL, '外套', 35.00, '包括夹克、风衣、大衣等', 3, 1),
(NULL, '裙子', 25.00, '包括连衣裙、半身裙等', 4, 1),
(NULL, '羽绒服', 50.00, '各类羽绒服', 5, 1),
(NULL, '床上用品', 40.00, '床单、被套、枕套等', 6, 1),
(NULL, '窗帘', 60.00, '各类窗帘', 7, 1),
(NULL, '鞋类', 30.00, '运动鞋、皮鞋等', 8, 1);

-- ----------------------------
-- 初始化数据 - 洗衣信息
-- ----------------------------
INSERT INTO `laundry_info` (`shop_id`, `clothes_type_id`, `name`, `price`, `original_price`, `description`, `wash_method`, `delivery_time`, `sort`, `status`) VALUES
(1, 1, '普通上衣清洗', 15.00, 20.00, '专业清洗，去污彻底', '机洗', '1-2天', 1, 1),
(1, 2, '裤子精洗', 18.00, 25.00, '深层清洁，呵护面料', '机洗', '1-2天', 2, 1),
(1, 3, '外套干洗', 35.00, 45.00, '专业干洗，保护衣物', '干洗', '2-3天', 3, 1),
(1, 5, '羽绒服专业清洗', 50.00, 68.00, '羽绒专用洗涤液，蓬松如新', '干洗', '3-5天', 4, 1),
(2, 1, '衬衫精洗熨烫', 20.00, 28.00, '清洗加熨烫，整洁如新', '机洗+熨烫', '1-2天', 1, 1),
(2, 4, '裙子专业护理', 28.00, 35.00, '温柔呵护，保持裙形', '手洗', '2-3天', 2, 1),
(2, 6, '床品四件套清洗', 60.00, 80.00, '深度清洁，杀菌除螨', '机洗', '2-3天', 3, 1),
(2, 8, '运动鞋清洗', 35.00, 45.00, '专业鞋类清洗，焕然一新', '手洗', '2-3天', 4, 1);

-- ----------------------------
-- 初始化数据 - 订单信息
-- ----------------------------
INSERT INTO `order_info` (`order_no`, `customer_id`, `shop_id`, `laundry_info_id`, `quantity`, `total_price`, `status`, `remark`, `pickup_address`, `delivery_address`, `create_time`) VALUES
('ORD202401150001', 2, 1, 1, 2, 30.00, 3, '请轻柔处理', '北京市朝阳区望京SOHO', '北京市朝阳区望京SOHO', '2024-01-15 10:30:00'),
('ORD202401160002', 2, 1, 4, 1, 50.00, 2, '羽绒服比较贵重', '北京市朝阳区望京SOHO', '北京市朝阳区望京SOHO', '2024-01-16 14:20:00'),
('ORD202401170003', 3, 2, 5, 3, 60.00, 1, '', '北京市海淀区五道口', '北京市海淀区五道口', '2024-01-17 09:15:00'),
('ORD202401180004', 3, 2, 7, 1, 60.00, 0, '床单有点大', '北京市海淀区五道口', '北京市海淀区五道口', '2024-01-18 16:45:00');

-- ----------------------------
-- 初始化数据 - 订单进度
-- ----------------------------
INSERT INTO `order_progress` (`order_id`, `status`, `description`, `operator_id`, `create_time`) VALUES
(1, 0, '订单已创建', 2, '2024-01-15 10:30:00'),
(1, 1, '已上门取件', 4, '2024-01-15 14:00:00'),
(1, 2, '正在洗涤中', 4, '2024-01-15 16:00:00'),
(1, 3, '已烘干完成', 4, '2024-01-15 18:00:00'),
(1, 4, '已整理打包', 4, '2024-01-15 19:00:00'),
(1, 5, '正在配送中', 4, '2024-01-16 09:00:00'),
(1, 6, '已送达客户', 4, '2024-01-16 10:30:00'),
(2, 0, '订单已创建', 2, '2024-01-16 14:20:00'),
(2, 1, '已上门取件', 4, '2024-01-16 17:00:00'),
(2, 2, '正在洗涤中', 4, '2024-01-17 10:00:00'),
(3, 0, '订单已创建', 3, '2024-01-17 09:15:00'),
(3, 1, '已上门取件', 5, '2024-01-17 14:00:00');

-- ----------------------------
-- 初始化数据 - 交流区帖子
-- ----------------------------
INSERT INTO `discussion` (`user_id`, `title`, `content`, `view_count`, `like_count`, `comment_count`, `status`) VALUES
(2, '洁净洗衣店服务真不错！', '上周试了一下洁净洗衣店的羽绒服清洗服务，效果非常好，衣服洗完蓬松如新，价格也很合理，强烈推荐！', 156, 23, 5, 1),
(3, '如何保养羊毛衫？', '请问大家有什么好的羊毛衫保养技巧吗？每次洗完都容易变形...', 89, 12, 8, 1),
(2, '分享一个去除顽固污渍的小技巧', '白醋加小苏打，对于咖啡渍和茶渍特别有效，大家可以试试~', 234, 45, 12, 1);

-- ----------------------------
-- 初始化数据 - 评论
-- ----------------------------
INSERT INTO `comment` (`discussion_id`, `user_id`, `parent_id`, `content`, `like_count`) VALUES
(1, 3, NULL, '同意！我也用过，确实不错', 5),
(1, 4, NULL, '感谢支持，我们会继续努力！', 8),
(2, 4, NULL, '羊毛衫建议手洗或者送到专业洗衣店处理', 10),
(2, 5, NULL, '可以使用羊毛专用洗涤剂，平铺晾干', 7),
(3, 3, NULL, '学到了，下次试试', 3);

-- ----------------------------
-- 初始化数据 - 系统公告
-- ----------------------------
INSERT INTO `announcement` (`title`, `content`, `type`, `status`) VALUES
('欢迎使用洗衣店管理系统', '亲爱的用户，欢迎使用洗衣店管理系统！我们致力于为您提供最优质的洗衣服务体验。', 0, 1),
('新年优惠活动公告', '新年来临之际，全场洗衣服务8折优惠！活动时间：2024年1月20日至2月20日，欢迎下单！', 1, 1),
('关于服务升级的通知', '为了给您提供更好的服务体验，本系统将于本周日凌晨2:00-4:00进行系统升级维护，届时服务将暂停，请提前安排好您的订单。', 0, 1);

-- ----------------------------
-- 初始化数据 - 客服消息
-- ----------------------------
INSERT INTO `customer_service` (`user_id`, `admin_id`, `content`, `type`, `is_read`) VALUES
(2, NULL, '请问羽绒服清洗需要多长时间？', 0, 1),
(2, 1, '您好，羽绒服清洗一般需要3-5个工作日，具体时间会根据衣物情况有所调整。', 1, 1),
(3, NULL, '我的订单什么时候能送到？', 0, 0);

SET FOREIGN_KEY_CHECKS = 1;
