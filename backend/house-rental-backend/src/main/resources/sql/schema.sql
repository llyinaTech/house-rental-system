-- 删除数据库
DROP DATABASE IF EXISTS house_rental;
-- 创建数据库
CREATE
    DATABASE IF NOT EXISTS house_rental DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE
    house_rental;

-- ==========================================
-- 1. 权限与用户模块 (RBAC)
-- ==========================================

-- 用户表
CREATE TABLE sys_user
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '加密密码',
    real_name   VARCHAR(50) COMMENT '真实姓名',
    user_type   TINYINT      NOT NULL COMMENT '用户类型',
    phone       VARCHAR(20) UNIQUE COMMENT '手机号',
    avatar      VARCHAR(255) COMMENT '头像URL',
    status      TINYINT  DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT  DEFAULT 0 COMMENT '逻辑删除: 0-未删, 1-已删'
) COMMENT '系统用户表';

-- 角色表
CREATE TABLE sys_role
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称 (管理员/房东/租客)',
    role_key  VARCHAR(50) NOT NULL UNIQUE COMMENT '角色权限字符 (admin/landlord/tenant)',
    remark    VARCHAR(255) COMMENT '备注'
) COMMENT '角色表';

-- 用户角色关联表
CREATE TABLE sys_user_role
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
) COMMENT '用户角色关联表';

-- 角色权限关联表
CREATE TABLE sys_role_permission
(
    role_id BIGINT NOT NULL,
    perm_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, perm_id)
) COMMENT '角色权限关联表';
-- 权限表
CREATE TABLE sys_permission
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id  BIGINT  DEFAULT 0 COMMENT '父权限ID(用于菜单树)',
    perm_name  VARCHAR(100) NOT NULL COMMENT '权限名称(菜单/按钮展示用)',
    perm_key   VARCHAR(100) NOT NULL UNIQUE COMMENT '权限标识, 如 house:list',
    type       TINYINT      NOT NULL COMMENT '类型: 1-目录, 2-菜单, 3-按钮/接口',
    path       VARCHAR(255) COMMENT '前端路由路径(菜单型权限使用)',
    component  VARCHAR(255) COMMENT '前端组件路径(可选)',
    method     VARCHAR(10) COMMENT '请求方法: GET/POST/PUT/DELETE(接口型用)',
    sort_order INT     DEFAULT 0 COMMENT '排序',
    visible    TINYINT DEFAULT 1 COMMENT '是否可见: 1-显示, 0-隐藏',
    status     TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用'
) COMMENT '权限/菜单表';

-- ==========================================
-- 2. 房源管理模块 (分层检索与展示)
-- ==========================================

-- 区域表 (树形结构，支持级联筛选)
CREATE TABLE sys_region
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id  BIGINT DEFAULT 0 COMMENT '父ID',
    name       VARCHAR(100) NOT NULL COMMENT '区域名称',
    level_type TINYINT COMMENT '层级: 1-省/市, 2-区, 3-街道/商圈',
    sort_order INT    DEFAULT 0 COMMENT '排序'
) COMMENT '区域商圈表';
-- 房源表
CREATE TABLE house_info
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    landlord_id  BIGINT         NOT NULL COMMENT '房东ID',
    title        VARCHAR(100)   NOT NULL COMMENT '房源标题',

    -- 核心检索字段 (建立索引)
    region_id    BIGINT COMMENT '所属区域ID',
    address      VARCHAR(255)   NOT NULL COMMENT '详细地址',
    price        DECIMAL(10, 2) NOT NULL COMMENT '月租金',
    area         DECIMAL(10, 2) NOT NULL COMMENT '面积',
    room_num     INT COMMENT '室',
    hall_num     INT COMMENT '厅',

    -- 详细配置
    floor_no     INT COMMENT '所在楼层',
    total_floor  INT COMMENT '总楼层',
    direction    VARCHAR(20) COMMENT '朝向',
    feature_tags JSON COMMENT '配套设施JSON: ["WiFi","空调"]',
    images       JSON COMMENT '图片列表JSON',
    description  TEXT COMMENT '详细描述',

    -- 状态机字段
    rent_status  TINYINT  DEFAULT 0 COMMENT '租赁状态: 0-未租, 1-已租, 2-下架',
    audit_status TINYINT  DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-通过, 2-拒绝',

    -- 乐观锁与统计
    version      INT      DEFAULT 0 COMMENT '乐观锁版本号(并发控制)',
    view_count   INT      DEFAULT 0 COMMENT '浏览量',

    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_price (price),
    INDEX idx_region (region_id),
    INDEX idx_room (room_num)
) COMMENT '房源信息表';

-- ==========================================
-- 3. 租赁合同与事务模块 (状态机核心)
-- ==========================================

-- 租赁合同表
CREATE TABLE lease_contract
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_no       VARCHAR(64)    NOT NULL UNIQUE COMMENT '合同编号',

    -- 关系人
    house_id          BIGINT         NOT NULL COMMENT '房源ID',
    landlord_id       BIGINT         NOT NULL COMMENT '房东ID',
    tenant_id         BIGINT         NOT NULL COMMENT '租客ID',

    -- 合同周期与金额
    start_date        DATE           NOT NULL COMMENT '生效日期',
    end_date          DATE           NOT NULL COMMENT '到期日期',
    sign_date         DATE COMMENT '签署日期',
    rent_amount       DECIMAL(10, 2) NOT NULL COMMENT '月租金',
    deposit_amount    DECIMAL(10, 2) NOT NULL COMMENT '押金',
    pay_period        TINYINT  DEFAULT 1 COMMENT '支付周期(月): 1-月付, 3-季付',

    -- 电子文件
    contract_file_url VARCHAR(255) COMMENT 'PDF文件地址',

    -- 核心状态机
    -- 流程: 待签署 -> 生效中 -> 即将到期 -> 已到期 / 已解约
    status            TINYINT  DEFAULT 0 COMMENT '状态: 0-待签署, 1-生效中, 2-已到期, 3-已解约, 4-已退租',

    remark            VARCHAR(255) COMMENT '备注',
    create_time       DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (house_id) REFERENCES house_info (id)
) COMMENT '租赁合同表';

-- ==========================================
-- 4. 财务与提醒模块 (自动化提醒)
-- ==========================================

-- 租金账单/提醒表
CREATE TABLE rent_bill
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_id      BIGINT         NOT NULL COMMENT '合同ID',
    tenant_id        BIGINT         NOT NULL,
    landlord_id      BIGINT         NOT NULL,

    period_desc      VARCHAR(50) COMMENT '期数描述 (如: 2025年6月租金)',
    bill_amount      DECIMAL(10, 2) NOT NULL COMMENT '应缴金额',
    due_date         DATE           NOT NULL COMMENT '最晚应缴日期 (用于定时任务扫描)',

    -- 支付状态
    pay_status       TINYINT  DEFAULT 0 COMMENT '状态: 0-未支付, 1-已支付, 2-逾期',
    pay_time         DATETIME COMMENT '实际支付时间',

    -- 提醒历史记录
    reminder_count   INT      DEFAULT 0 COMMENT '已发送提醒次数',
    last_remind_time DATETIME COMMENT '上次提醒时间',

    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_due_status (due_date, pay_status) -- 优化定时任务查询
) COMMENT '租金账单提醒表';

-- ==========================================
-- 5. 运维与服务模块 (报修与日志)
-- ==========================================

-- 报修工单表
CREATE TABLE repair_order
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    house_id        BIGINT       NOT NULL,
    tenant_id       BIGINT       NOT NULL,

    title           VARCHAR(100) NOT NULL COMMENT '报修标题',
    content         TEXT         NOT NULL COMMENT '报修详情',
    images          JSON COMMENT '报修图片JSON',

    -- 处理流程
    status          TINYINT  DEFAULT 0 COMMENT '状态: 0-待处理, 1-处理中, 2-已完结, 3-已取消',
    result_feedback VARCHAR(255) COMMENT '处理结果反馈',

    -- 闭环记录
    process_log     JSON COMMENT '流转日志: [{"time": "...", "action": "房东接单"}]',

    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME ON UPDATE CURRENT_TIMESTAMP
) COMMENT '房屋报修表';

-- 系统操作日志 (用于安全审计)
CREATE TABLE sys_log
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT COMMENT '操作人ID',
    username    VARCHAR(50) COMMENT '操作人用户名',
    module      VARCHAR(50) COMMENT '操作模块 (用户管理/房源管理/合同管理/财务管理/系统登录)',
    action      VARCHAR(50) COMMENT '操作类型 (新增/修改/删除/查询/登录/导出)',
    ip          VARCHAR(50) COMMENT '操作IP地址',
    status      VARCHAR(20) COMMENT '操作状态: 成功/失败',
    detail      TEXT COMMENT '详细信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间'
) COMMENT '系统操作日志表';

-- 统计报表中间表 (可选，用于补充要求6的高效可视化)
-- 如果数据量不大，直接统计业务表即可；若数据量大，建议建立此表做每日聚合
CREATE TABLE daily_stats
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date        DATE NOT NULL COMMENT '统计日期',
    active_contracts INT            DEFAULT 0 COMMENT '在租合同数',
    total_income     DECIMAL(12, 2) DEFAULT 0.00 COMMENT '当日租金收入',
    new_repairs      INT            DEFAULT 0 COMMENT '新增报修数',
    UNIQUE KEY idx_date (stat_date)
) COMMENT '每日运营统计表';

-- ==========================================
-- 6. 公告管理模块
-- ==========================================

-- 公告表
CREATE TABLE announcement
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title        VARCHAR(200) NOT NULL COMMENT '公告标题',
    content      TEXT         NOT NULL COMMENT '公告内容',
    user_id      BIGINT       NOT NULL COMMENT '发布人ID',
    user_name    VARCHAR(100) NOT NULL COMMENT '发布人姓名',
    publish_time DATETIME COMMENT '发布时间',
    status       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布, 2-已撤销',
    create_time  DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '公告表';
