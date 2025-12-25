# 房屋租赁系统前端

## 项目简介
- 基于 `Vue 3 + Vite + Element Plus` 的房屋租赁管理系统前端。
- 风格参考 Ruoyi（深色侧边栏、白色头部、蓝色主色），功能与布局按企业后台规范实现。

## 技术栈
- `Vue 3`、`Vite`
- `Element Plus`（UI 组件库）
- `Vue Router`（路由与嵌套路由）
- `Pinia`（状态管理）
- `Axios`（HTTP 请求封装）

## 功能模块
- 登录与鉴权：用户名/密码 + 身份选择（租客/房东/管理员），登录后进入仪表盘。
- 管理布局：深色侧边栏、顶部导航、面包屑、侧边栏收起/展开。
- 仪表盘：统计卡片、图表占位、待办列表占位。
- 房源管理：查询与列表占位，后续可接入后端分页与筛选。

## 目录结构
```
house-rental-frontend/
├─ public/
├─ src/
│  ├─ api/            # 请求封装
│  │   └─ request.js
│  ├─ assets/
│  ├─ components/
│  ├─ layouts/        # 后台主布局
│  │   └─ BasicLayout.vue
│  ├─ router/         # 路由配置（嵌套路由）
│  │   └─ index.js
│  ├─ stores/         # Pinia 状态（鉴权）
│  │   └─ auth.js
│  ├─ views/          # 页面视图
│  │   ├─ Login.vue
│  │   ├─ Dashboard.vue
│  │   └─ Listings.vue
│  ├─ App.vue         # 根组件（仅 router-view）
│  └─ main.js         # 入口文件
├─ .env.development   # 开发环境变量
├─ vite.config.js     # Vite 配置（路径别名 @）
├─ package.json
└─ README.md
```

## 环境要求
- 推荐 `Node.js >= 16`
- 包管理器：`npm`

## 快速开始
1. 安装依赖
   - `npm install`
2. 开发启动
   - `npm run dev`
   - 默认访问 `http://127.0.0.1:5173/`（端口占用时自动切换）
3. 构建与预览
   - `npm run build`
   - `npm run preview`

## 环境变量
- 在 `.env.development` 中配置：
  - `VITE_API_BASE_URL=http://localhost:8080/api`
  - 生产环境可使用 `.env.production` 自行配置。

## 路由与布局
- 登录页独立路由：`/login`
- 主布局采用嵌套路由：
  - 父路由 `/` 使用 `BasicLayout.vue`
  - 子路由：`/dashboard`、`/listings` 等均作为 `children` 挂载
- 路由守卫：未登录访问鉴权页面时重定向至登录页
  - 参考 `src/router/index.js:21-26`

## 鉴权与请求
- 鉴权状态：`token` 与 `user` 存储于 Pinia，并持久化至 `localStorage`
  - 参考 `src/stores/auth.js:3-27`
- Axios 请求拦截：自动附加 `Authorization: Bearer <token>`
  - 参考 `src/api/request.js:10-16`
- 响应拦截：`401` 自动登出并跳转登录页
  - 参考 `src/api/request.js:18-28`

## UI 与风格
- 侧边栏深色背景 `#304156`，主色 `#409EFF`，收起/展开切换。
- 菜单项图标为内联 SVG，收起时显示图标，展开显示图标+文字。
- 登录页 PC 端双栏布局，支持身份下拉与基础校验。

## 开发规范（建议）
- 路由页面统一放在 `views/`，通用布局放在 `layouts/`。
- 组件保持无副作用，业务状态统一经 Pinia 管理。
- API 调用经 `src/api/request.js` 封装，严禁在页面中直接使用裸 `axios.create`。
- 严格避免提交任何敏感信息（密钥、Token 等）。

## 后续接入
- 将登录请求改为真实接口：`POST /auth/login`，返回 `token` 与 `user(role)`。
- 接入房源查询与分页筛选：`GET /listings` 支持条件组合与分页。
- 根据 `role` 动态控制菜单与权限（后端返回菜单树 → 生成路由）。

## 常见问题
- 启动提示依赖版本警告：建议升级 Node 至 `>=16`；不影响开发但推荐升级以减少警告。
- 无法解析路径 `@`：检查 `vite.config.js` 的 `resolve.alias` 设置是否正确。

## 脚本
- `npm run dev` 本地开发
- `npm run build` 生产构建
- `npm run preview` 本地预览构建产物

---
如需我继续完善“角色-菜单-权限”的联动与后端接口对接，请告知接口定义即可快速接入。