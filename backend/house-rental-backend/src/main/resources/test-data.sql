-- 测试数据初始化脚本
USE house_rental;

-- 插入角色数据
INSERT INTO sys_role (id, role_name, role_key, remark) VALUES
(1, '系统管理员', 'admin', '系统管理员，拥有所有权限'),
(2, '房东', 'landlord', '房东角色，可以管理房源'),
(3, '租客', 'tenant', '租客角色，可以查看和租赁房源');

-- 插入测试用户
-- 注意：密码都是 '123456' 经过BCrypt加密后的值
-- 使用BCryptPasswordEncoder默认strength=10加密
INSERT INTO sys_user (id, username, password, real_name, user_type, phone, status, deleted) VALUES
(1, 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '系统管理员', 1, '13800000001', 1, 0),
(2, 'landlord', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '张房东', 2, '13800000002', 1, 0),
(3, 'tenant', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '李租客', 3, '13800000003', 1, 0);

-- 插入用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), -- admin用户拥有admin角色
(2, 2), -- landlord用户拥有landlord角色
(3, 3); -- tenant用户拥有tenant角色

-- 插入测试日志数据
INSERT INTO sys_log (id, user_id, username, module, action, ip, status, detail, create_time) VALUES
(1001, 1, 'admin', '用户管理', '新增', '127.0.0.1', '成功', '新增用户: test_user', '2023-10-25 10:00:00'),
(1002, 2, 'landlord1', '房源管理', '修改', '192.168.1.10', '成功', '更新房源价格: 2500 -> 2600', '2023-10-25 10:15:00'),
(1003, 1, 'admin', '合同管理', '删除', '127.0.0.1', '失败', '删除合同失败: 权限不足', '2023-10-25 11:30:00'),
(1004, 3, 'tenant1', '系统登录', '登录', '192.168.1.20', '成功', '用户登录成功', '2023-10-25 09:00:00'),
(1005, 1, 'admin', '财务管理', '查询', '127.0.0.1', '成功', '查询本月报表', '2023-10-24 16:00:00');
