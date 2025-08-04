# 项目技术与结构说明

## 技术栈
- Kotlin Multiplatform (KMP)
- Jetpack Compose Multiplatform (JCM)
- Compose Material3 组件库
- 资源管理：composeResources
- 多平台支持：Android、JVM、WASM

## 目录结构
- 根目录
  - `build.gradle.kts`、`settings.gradle.kts`：KMP项目配置
  - `composeApp/`：主模块
    - `src/`
      - `commonMain/`
        - `kotlin/`：共享Kotlin代码（如App.kt、页面、业务逻辑等）
          - `cn/edu/ysu/ciallo/cardbalance/`：卡余额相关模块
            - `CardBalanceData.kt`：卡余额数据模型、错误类型、结果封装
            - `CardBalanceRepository.kt`：仓库接口，含Mock与Remote实现
            - `CardBalanceViewModel.kt`：卡余额ViewModel
            - `NetworkCookieManager.kt`：多API共用Cookie管理器
        - `composeResources/`：Compose资源
      - `androidMain/`：Android专用代码与资源
      - `jvmMain/`：JVM专用代码
      - `wasmJsMain/`：WASM/JS专用代码与资源
    - `build/`：编译输出
- `demo/`：演示图片等
- `commonTest/`
  - `kotlin/cn/edu/ysu/ciallo/cardbalance/CardBalanceRepositoryTest.kt`：卡余额仓库单元测试

## 主要功能模块
- App.kt：应用入口，包含底部导航与页面切换
- HomePage：主页，展示日程、电费、借书、图书馆、卡余额、校园网等信息卡片
- ClubRecommendPage：社团推荐页，展示社团列表
- CardBalance：卡余额模块，支持本地模拟与真实API切换，完善错误处理

## 组件与页面结构
- 使用`Scaffold`实现整体布局，`NavigationBar`实现底部导航
- 各功能区块用`Card`+`Column`+`Row`组合
- 列表用`LazyColumn`实现
- 图标用`Icon`（可后续替换为自定义资源）

## 数据层与状态管理
- 数据与UI分离：业务数据通过Repository接口获取，支持后端API对接，UI仅负责展示。
- ViewModel：如HomeViewModel、CardBalanceViewModel，负责数据加载与状态管理，支持多平台。
- 数据模型：如HomeData、LibraryStatus、CardBalanceData，定义各区块展示数据结构。
- Repository接口：如HomeRepository、CardBalanceRepository，支持本地假数据与后端API切换。
- Cookie管理：所有API共用NetworkCookieManager，便于多API登录态复用。
- 本地/远程数据切换：通过依赖注入或构造参数选择Mock或Remote实现，调试时用Mock，单元测试/真实使用用Remote。

### 相关目录
- `composeApp/src/commonMain/kotlin/cn/edu/ysu/ciallo/home/`
  - `HomeData.kt`：首页数据模型与仓库接口
  - `HomeViewModel.kt`：首页ViewModel
- `composeApp/src/commonMain/kotlin/cn/edu/ysu/ciallo/cardbalance/`
  - `CardBalanceData.kt`：卡余额数据模型、错误类型、结果封装
  - `CardBalanceRepository.kt`：仓库接口，含Mock与Remote实现
  - `CardBalanceViewModel.kt`：卡余额ViewModel
  - `NetworkCookieManager.kt`：多API共用Cookie管理器
- `composeApp/src/commonTest/kotlin/cn/edu/ysu/ciallo/cardbalance/`
  - `CardBalanceRepositoryTest.kt`：卡余额仓库单元测试

### 示例流程
- UI页面（如HomePage）通过ViewModel获取数据
- ViewModel调用Repository异步加载数据
- Repository可对接本地假数据或后端API
- UI根据数据渲染Material3风格界面
- 所有API共用Cookie，便于统一登录态管理

## 约定
- 业务逻辑与UI分离，后续可扩展ViewModel等架构
- 资源与多平台适配可在各`*Main`目录下实现
- API接口支持完善错误处理（如未登录、网络失败等）
- 支持本地模拟数据与真实API切换，便于调试与测试

---
如需补充具体页面、功能、资源等细节，请在本文件继续记录。
