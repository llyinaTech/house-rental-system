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

-- 插入公告数据
INSERT INTO announcement (id, title, content, user_id, user_name, publish_time, status, create_time, update_time) VALUES
(1, '系统维护通知', '尊敬的用户，系统将于本周六晚上22:00至周日凌晨2:00进行维护，期间暂停服务，敬请谅解。', 1, '管理员', '2023-10-25 14:00:00', 1, '2023-10-25 14:00:00', '2023-10-25 14:00:00'),
(2, '房租调整公告', '根据市场情况，从下月起部分房源租金将进行调整，具体调整信息请关注个人通知。', 1, '管理员', '2023-10-25 15:00:00', 1, '2023-10-25 15:00:00', '2023-10-25 15:00:00'),
(3, '节假日放假安排', '春节期间，客服中心将暂停服务，如有紧急情况请拨打24小时热线。', 1, '管理员', '2023-10-25 16:00:00', 1, '2023-10-25 16:00:00', '2023-10-25 16:00:00');

-- 插入房源数据
INSERT INTO house_info (id, landlord_id, title, region_id, address, price, area, room_num, hall_num, floor_no, total_floor, direction, feature_tags, images, description, rent_status, audit_status, create_time) VALUES
(1, 2, '温馨一室一厅', 1, '北京市朝阳区建国路88号', 3500.00, 45.00, 1, 1, 5, 18, '南', '["空调","WiFi","洗衣机","冰箱"]', '["https://example.com/image1.jpg","https://example.com/image2.jpg"]', '交通便利，周边配套齐全，适合上班族居住。', 0, 1, '2023-10-25 10:00:00'),
(2, 2, '宽敞两室一厅', 1, '北京市朝阳区国贸CBD', 5200.00, 75.00, 2, 1, 12, 25, '南北通透', '["空调","WiFi","洗衣机","冰箱","电视"]', '["https://example.com/image3.jpg","https://example.com/image4.jpg"]', '精装修，家具家电齐全，拎包入住。', 0, 1, '2023-10-25 10:00:00'),
(3, 2, '豪华三室两厅', 2, '北京市海淀区中关村', 8000.00, 120.00, 3, 2, 8, 20, '南', '["空调","WiFi","洗衣机","冰箱","电视","沙发","餐桌"]', '["https://example.com/image5.jpg","https://example.com/image6.jpg"]', '学区房，周边有名校，适合家庭居住。', 1, 1, '2023-10-25 10:00:00');

-- 插入租赁合同数据
INSERT INTO lease_contract (id, contract_no, house_id, landlord_id, tenant_id, start_date, end_date, sign_date, rent_amount, deposit_amount, pay_period, contract_file_url, status, remark, create_time) VALUES
(1, 'CONTRACT20231025001', 1, 2, 3, '2023-10-01', '2024-09-30', '2023-09-28', 3500.00, 7000.00, 1, 'https://example.com/contract/1.pdf', 1, '首次租赁，租客信用良好', '2023-09-28 10:00:00'),
(2, 'CONTRACT20231025002', 2, 2, 3, '2023-11-01', '2024-10-31', '2023-10-20', 5200.00, 10400.00, 1, 'https://example.com/contract/2.pdf', 0, '待签署合同', '2023-10-20 14:30:00'),
(3, 'CONTRACT20231025003', 3, 2, 3, '2023-12-01', '2024-11-30', '2023-11-15', 8000.00, 16000.00, 3, 'https://example.com/contract/3.pdf', 2, '季度付款，已到期', '2023-11-15 09:15:00');

-- 插入报修工单数据
INSERT INTO repair_order (id, house_id, tenant_id, title, content, images, status, result_feedback, process_log, create_time, update_time) VALUES
(1, 1, 3, '厨房水龙头漏水', '厨房水龙头持续漏水，需要更换密封圈', '["https://example.com/repair/1_1.jpg","https://example.com/repair/1_2.jpg"]', 2, '已更换水龙头密封圈，漏水问题解决', '[{"time": "2023-10-26 09:00:00", "action": "房东接单"}, {"time": "2023-10-26 10:30:00", "action": "已安排维修师傅"}, {"time": "2023-10-26 14:00:00", "action": "维修完成"}]', '2023-10-26 08:30:00', '2023-10-26 14:00:00'),
(2, 2, 3, '客厅空调不制冷', '客厅空调开启后不制冷，疑似缺少制冷剂', '["https://example.com/repair/2_1.jpg"]', 1, '正在联系专业维修人员', '[{"time": "2023-10-27 10:00:00", "action": "房东接单"}, {"time": "2023-10-27 11:00:00", "action": "联系维修公司"}]', '2023-10-27 09:15:00', '2023-10-27 11:00:00'),
(3, 1, 3, '卫生间排风扇故障', '卫生间排风扇无法启动，需要检修', '["https://example.com/repair/3_1.jpg","https://example.com/repair/3_2.jpg","https://example.com/repair/3_3.jpg"]', 0, NULL, '[{"time": "2023-10-28 14:20:00", "action": "提交报修申请"}]', '2023-10-28 14:15:00', '2023-10-28 14:15:00'),
(4, 3, 3, '卧室门锁损坏', '卧室门锁转动不灵活，钥匙难以插入', '["https://example.com/repair/4_1.jpg"]', 2, '已更换门锁，可正常使用', '[{"time": "2023-10-29 08:00:00", "action": "房东接单"}, {"time": "2023-10-29 15:00:00", "action": "维修完成"}]', '2023-10-29 07:45:00', '2023-10-29 15:00:00'),
(5, 2, 3, '阳台窗户滑轮损坏', '阳台推拉窗滑轮损坏，窗户难以开关', '["https://example.com/repair/5_1.jpg","https://example.com/repair/5_2.jpg"]', 3, '租客自行解决', '[{"time": "2023-10-30 10:00:00", "action": "房东接单"}, {"time": "2023-10-30 10:15:00", "action": "评估维修成本较高，建议租客自行处理"}]', '2023-10-30 09:30:00', '2023-10-30 10:15:00');

-- 插入租金账单数据
INSERT INTO rent_bill (id, contract_id, tenant_id, landlord_id, period_desc, bill_amount, due_date, pay_status, pay_time, reminder_count, last_remind_time, create_time) VALUES
(1, 1, 3, 2, '2023年10月租金', 3500.00, '2023-10-30', 1, '2023-10-28 10:00:00', 0, NULL, '2023-10-01 00:00:00'),
(2, 1, 3, 2, '2023年11月租金', 3500.00, '2023-11-30', 1, '2023-11-25 14:30:00', 0, NULL, '2023-11-01 00:00:00'),
(3, 1, 3, 2, '2023年12月租金', 3500.00, '2023-12-30', 1, '2023-12-28 09:15:00', 0, NULL, '2023-12-01 00:00:00'),
(4, 1, 3, 2, '2024年1月租金', 3500.00, '2024-01-30', 0, NULL, 1, '2024-01-30 10:00:00', '2024-01-01 00:00:00'),
(5, 1, 3, 2, '2024年2月租金', 3500.00, '2024-02-28', 2, NULL, 3, '2024-02-29 15:00:00', '2024-02-01 00:00:00'),
(6, 3, 3, 2, '2023年12月租金', 8000.00, '2023-12-30', 1, '2023-12-25 16:20:00', 0, NULL, '2023-12-01 00:00:00'),
(7, 3, 3, 2, '2024年1-3月租金', 24000.00, '2024-03-30', 1, '2024-01-15 11:45:00', 0, NULL, '2024-01-01 00:00:00'),
(8, 3, 3, 2, '2024年4-6月租金', 24000.00, '2024-06-30', 0, NULL, 0, NULL, '2024-04-01 00:00:00');

-- 插入区域数据
INSERT INTO sys_region (id, parent_id, name, level_type, sort_order) VALUES
(1, 0, '北京市', 1, 1),
(2, 0, '上海市', 1, 2),
(3, 0, '广州市', 1, 3),
(4, 0, '深圳市', 1, 4),
(5, 1, '朝阳区', 2, 1),
(6, 1, '海淀区', 2, 2),
(7, 1, '东城区', 2, 3),
(8, 1, '西城区', 2, 4),
(9, 2, '浦东新区', 2, 1),
(10, 2, '徐汇区', 2, 2),
(11, 5, '国贸', 3, 1),
(12, 5, '三里屯', 3, 2),
(13, 5, '望京', 3, 3),
(14, 6, '中关村', 3, 1),
(15, 6, '五道口', 3, 2),
(16, 6, '上地', 3, 3),
(17, 9, '陆家嘴', 3, 1),
(18, 9, '张江', 3, 2);