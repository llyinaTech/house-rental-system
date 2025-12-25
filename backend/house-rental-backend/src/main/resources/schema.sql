-- 创建数据库
CREATE DATABASE IF NOT EXISTS `house_rental` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `house_rental`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `user_type` INT(11) DEFAULT 1 COMMENT '用户类型（1:租客 2:房东 3:管理员）',
    `status` INT(11) DEFAULT 1 COMMENT '账号状态（0:禁用 1:正常）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 房屋表
CREATE TABLE IF NOT EXISTS `house` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(100) NOT NULL COMMENT '房屋标题',
    `address` VARCHAR(200) NOT NULL COMMENT '房屋地址',
    `rent` DECIMAL(10,2) NOT NULL COMMENT '租金（元/月）',
    `area` DECIMAL(10,2) DEFAULT NULL COMMENT '房屋面积（平方米）',
    `rooms` INT(11) DEFAULT NULL COMMENT '房间数',
    `bathrooms` INT(11) DEFAULT NULL COMMENT '卫生间数',
    `floor` VARCHAR(20) DEFAULT NULL COMMENT '楼层',
    `type` INT(11) DEFAULT 1 COMMENT '房屋类型（1:整租 2:合租）',
    `status` INT(11) DEFAULT 1 COMMENT '房屋状态（1:可租 2:已租 3:维修中）',
    `description` TEXT COMMENT '房屋描述',
    `landlord_id` BIGINT(20) NOT NULL COMMENT '房东ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_landlord_id` (`landlord_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房屋表';

-- 插入测试数据
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `user_type`, `status`) VALUES
('admin', '123456', '管理员', '13800138000', 'admin@example.com', 3, 1),
('landlord1', '123456', '张房东', '13800138001', 'landlord1@example.com', 2, 1),
('tenant1', '123456', '李租客', '13800138002', 'tenant1@example.com', 1, 1);

INSERT INTO `house` (`title`, `address`, `rent`, `area`, `rooms`, `bathrooms`, `floor`, `type`, `status`, `description`, `landlord_id`) VALUES
('精装两室一厅', '北京市朝阳区望京SOHO', 5000.00, 80.00, 2, 1, '10/20', 1, 1, '精装修，家具家电齐全，交通便利', 2),
('舒适三室两厅', '北京市海淀区中关村', 8000.00, 120.00, 3, 2, '15/30', 1, 1, '南北通透，采光好，周边配套完善', 2),
('温馨单间', '北京市朝阳区三里屯', 2500.00, 20.00, 1, 1, '5/10', 2, 1, '合租房间，独立卫生间，拎包入住', 2);
