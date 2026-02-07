-- ----------------------------
-- 1. 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id`          bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '字典主键',
  `dict_name`        varchar(100)    DEFAULT ''                 COMMENT '字典名称',
  `dict_type`        varchar(100)    DEFAULT ''                 COMMENT '字典类型',
  `status`           int(1)          DEFAULT '1'                COMMENT '状态（1正常 0停用）',
  `remark`           varchar(500)    DEFAULT NULL               COMMENT '备注',
  `create_time`      datetime        DEFAULT NULL               COMMENT '创建时间',
  `update_time`      datetime        DEFAULT NULL               COMMENT '更新时间',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ----------------------------
-- 2. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code`        bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '字典编码',
  `dict_sort`        int(4)          DEFAULT '0'                COMMENT '字典排序',
  `dict_label`       varchar(100)    DEFAULT ''                 COMMENT '字典标签',
  `dict_value`       varchar(100)    DEFAULT ''                 COMMENT '字典键值',
  `dict_type`        varchar(100)    DEFAULT ''                 COMMENT '字典类型',
  `css_class`        varchar(100)    DEFAULT NULL               COMMENT '样式属性（其他样式扩展）',
  `list_class`       varchar(100)    DEFAULT NULL               COMMENT '表格回显样式',
  `is_default`       char(1)         DEFAULT 'N'                COMMENT '是否默认（Y是 N否）',
  `status`           int(1)          DEFAULT '1'                COMMENT '状态（1正常 0停用）',
  `remark`           varchar(500)    DEFAULT NULL               COMMENT '备注',
  `create_time`      datetime        DEFAULT NULL               COMMENT '创建时间',
  `update_time`      datetime        DEFAULT NULL               COMMENT '更新时间',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ----------------------------
-- 初始化部分默认字典数据
-- ----------------------------

-- 用户性别
INSERT INTO sys_dict_type VALUES(1, '用户性别', 'sys_user_sex', 1, '用户性别列表', NOW(), NOW());
INSERT INTO sys_dict_data VALUES(1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', 1, '性别男', NOW(), NOW());
INSERT INTO sys_dict_data VALUES(2, 2, '女', '1', 'sys_user_sex', '', '', 'N', 1, '性别女', NOW(), NOW());
INSERT INTO sys_dict_data VALUES(3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', 1, '性别未知', NOW(), NOW());

-- 菜单状态
INSERT INTO sys_dict_type VALUES(2, '菜单状态', 'sys_show_hide', 1, '菜单状态列表', NOW(), NOW());
INSERT INTO sys_dict_data VALUES(4, 1, '显示', '1', 'sys_show_hide', '', 'primary', 'Y', 1, '显示菜单', NOW(), NOW());
INSERT INTO sys_dict_data VALUES(5, 2, '隐藏', '0', 'sys_show_hide', '', 'danger', 'N', 1, '隐藏菜单', NOW(), NOW());

-- 系统开关
INSERT INTO sys_dict_type VALUES(3, '系统开关', 'sys_normal_disable', 1, '系统开关列表', NOW(), NOW());
INSERT INTO sys_dict_data VALUES(6, 1, '正常', '1', 'sys_normal_disable', '', 'primary', 'Y', 1, '正常状态', NOW(), NOW());
INSERT INTO sys_dict_data VALUES(7, 2, '停用', '0', 'sys_normal_disable', '', 'danger', 'N', 1, '停用状态', NOW(), NOW());
