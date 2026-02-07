-- Clean up existing permissions
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE sys_role_permission;
TRUNCATE TABLE sys_permission;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. System Management (Dir)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, sort_order, visible, status) VALUES (1, 0, '系统管理', 'system', 1, 'system', 1, 1, 1);

-- 1.1 User Management (Menu)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, component, sort_order, visible, status) VALUES (11, 1, '用户管理', 'system:user', 2, 'users', 'system/UserManagement', 1, 1, 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, sort_order, visible, status) VALUES 
(1101, 11, '用户查询', 'system:user:query', 3, 1, 0, 1),
(1102, 11, '用户新增', 'system:user:add', 3, 2, 0, 1),
(1103, 11, '用户编辑', 'system:user:edit', 3, 3, 0, 1),
(1104, 11, '用户删除', 'system:user:remove', 3, 4, 0, 1);

-- 1.2 Role Management (Menu)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, component, sort_order, visible, status) VALUES (12, 1, '角色管理', 'system:role', 2, 'roles', 'system/RoleManagement', 2, 1, 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, sort_order, visible, status) VALUES 
(1201, 12, '角色查询', 'system:role:query', 3, 1, 0, 1),
(1202, 12, '角色新增', 'system:role:add', 3, 2, 0, 1),
(1203, 12, '角色编辑', 'system:role:edit', 3, 3, 0, 1),
(1204, 12, '角色删除', 'system:role:remove', 3, 4, 0, 1);

-- 1.3 Permission Management (Menu)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, component, sort_order, visible, status) VALUES (13, 1, '权限管理', 'system:permission', 2, 'permissions', 'system/PermissionManagement', 3, 1, 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, sort_order, visible, status) VALUES 
(1301, 13, '权限查询', 'system:permission:query', 3, 1, 0, 1),
(1302, 13, '权限新增', 'system:permission:add', 3, 2, 0, 1),
(1303, 13, '权限编辑', 'system:permission:edit', 3, 3, 0, 1),
(1304, 13, '权限删除', 'system:permission:remove', 3, 4, 0, 1);

-- 1.4 Notice Management (Menu)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, component, sort_order, visible, status) VALUES (14, 1, '公告管理', 'system:notice', 2, 'notices', 'system/NoticeManagement', 4, 1, 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, sort_order, visible, status) VALUES 
(1401, 14, '公告查询', 'system:notice:query', 3, 1, 0, 1),
(1402, 14, '公告新增', 'system:notice:add', 3, 2, 0, 1),
(1403, 14, '公告编辑', 'system:notice:edit', 3, 3, 0, 1),
(1404, 14, '公告删除', 'system:notice:remove', 3, 4, 0, 1);

-- 1.5 Log Management (Menu)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, component, sort_order, visible, status) VALUES (15, 1, '日志管理', 'system:log', 2, 'logs', 'system/LogManagement', 5, 1, 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, sort_order, visible, status) VALUES 
(1501, 15, '日志查询', 'system:log:query', 3, 1, 0, 1);

-- 1.6 Dictionary Management (Menu)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, component, sort_order, visible, status) VALUES (16, 1, '字典管理', 'system:dict', 2, 'dict', 'system/DictManagement', 6, 1, 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, sort_order, visible, status) VALUES 
(1601, 16, '字典查询', 'system:dict:query', 3, 1, 0, 1),
(1602, 16, '字典新增', 'system:dict:add', 3, 2, 0, 1),
(1603, 16, '字典编辑', 'system:dict:edit', 3, 3, 0, 1),
(1604, 16, '字典删除', 'system:dict:remove', 3, 4, 0, 1);

-- 2. House Management (Dir)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, sort_order, visible, status) VALUES (2, 0, '房源管理', 'house', 1, 'house', 2, 1, 1);

-- 2.1 House List (Menu)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, component, sort_order, visible, status) VALUES (21, 2, '房源信息', 'house:info', 2, 'list', 'house/Listings', 1, 1, 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, sort_order, visible, status) VALUES 
(2101, 21, '房源查询', 'house:info:query', 3, 1, 0, 1),
(2102, 21, '房源新增', 'house:info:add', 3, 2, 0, 1),
(2103, 21, '房源编辑', 'house:info:edit', 3, 3, 0, 1),
(2104, 21, '房源删除', 'house:info:remove', 3, 4, 0, 1),
(2105, 21, '房源审核', 'house:info:audit', 3, 5, 0, 1);

-- 3. Contract Management (Dir)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, sort_order, visible, status) VALUES (3, 0, '合同管理', 'contract', 1, 'contract', 3, 1, 1);

-- 3.1 Contract List (Menu)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, component, sort_order, visible, status) VALUES (31, 3, '合同列表', 'contract:list', 2, 'list', 'contract/ContractList', 1, 1, 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, sort_order, visible, status) VALUES 
(3101, 31, '合同查询', 'contract:list:query', 3, 1, 0, 1),
(3102, 31, '合同新增', 'contract:list:add', 3, 2, 0, 1),
(3103, 31, '合同编辑', 'contract:list:edit', 3, 3, 0, 1),
(3104, 31, '合同删除', 'contract:list:remove', 3, 4, 0, 1),
(3105, 31, '合同解约', 'contract:list:terminate', 3, 5, 0, 1);

-- 4. Finance Management (Dir)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, sort_order, visible, status) VALUES (4, 0, '财务管理', 'finance', 1, 'finance', 4, 1, 1);

-- 4.1 Rent Bills (Menu)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, component, sort_order, visible, status) VALUES (41, 4, '租金账单', 'finance:bill', 2, 'bills', 'finance/RentBills', 1, 1, 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, sort_order, visible, status) VALUES 
(4101, 41, '账单查询', 'finance:bill:query', 3, 1, 0, 1),
(4102, 41, '账单导出', 'finance:bill:export', 3, 2, 0, 1);

-- 4.2 Statistics (Menu)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, component, sort_order, visible, status) VALUES (42, 4, '财务统计', 'finance:stats', 2, 'stats', 'finance/Statistics', 2, 1, 1);

-- 5. Service Management (Dir)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, sort_order, visible, status) VALUES (5, 0, '服务管理', 'service', 1, 'service', 5, 1, 1);

-- 5.1 Repair Orders (Menu)
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, path, component, sort_order, visible, status) VALUES (51, 5, '报修工单', 'service:repair', 2, 'repairs', 'service/RepairOrders', 1, 1, 1);
INSERT INTO sys_permission (id, parent_id, perm_name, perm_key, type, sort_order, visible, status) VALUES 
(5101, 51, '工单查询', 'service:repair:query', 3, 1, 0, 1),
(5102, 51, '工单处理', 'service:repair:process', 3, 2, 0, 1);

-- Ensure Admin Role exists
-- Note: Assuming sys_role table structure: id, role_name, role_key, remark
-- Using INSERT IGNORE or ON DUPLICATE KEY UPDATE to avoid errors if it exists
INSERT INTO sys_role (id, role_name, role_key, remark) VALUES (1, '超级管理员', 'admin', '系统超级管理员') ON DUPLICATE KEY UPDATE role_name='超级管理员';

-- Assign All Permissions to Admin (Role ID 1)
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT 1, id FROM sys_permission;
