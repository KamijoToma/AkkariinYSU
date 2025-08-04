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

## 远程API对接层设计 (YSU E-Hall)

为了实现从燕山大学网上服务大厅 (E-Hall) 获取真实数据，我们将设计并实现一个独立的远程API对接层。该层将负责处理网络请求、用户认证、数据解析等任务，并与现有的 `Repository` 层进行集成。

### 核心组件

1.  **`YsuEhallApi`**: 一个中心化的API客户端类，封装了与E-Hall系统交互的所有网络请求逻辑。
    -   **技术选型**: 使用 Ktor 客户端库，因为它提供了强大的多平台网络请求能力。
    -   **主要职责**:
        -   管理 `HttpClient` 实例，配置通用请求头、Cookie存储等。
        -   实现登录流程 (`login` 方法)，包括获取登录页面、解析表单参数、加密密码以及处理登录响应。
        -   提供各个业务接口的调用方法 (例如 `getCardBalance`)。
    -   **位置**: `composeApp/src/commonMain/kotlin/cn/edu/ysu/ciallo/ysu/YsuEhallApi.kt`

2.  **`YsuEhallApiFactory`**: 一个工厂类，用于创建和配置 `YsuEhallApi` 的实例。
    -   **职责**:
        -   提供一个单例的 `YsuEhallApi` 实例，确保应用内共享同一个登录会话 (Session)。
        -   管理用户凭据 (用户名、密码)，在实际应用中可以对接安全存储。
    -   **位置**: `composeApp/src/commonMain/kotlin/cn/edu/ysu/ciallo/ysu/YsuEhallApiFactory.kt`

3.  **`RemoteCardBalanceRepository`**: `CardBalanceRepository` 接口的远程实现。
    -   **职责**:
        -   依赖 `YsuEhallApi` 来获取一卡通余额。
        -   在调用接口前检查登录状态，如果未登录则自动执行登录。
        -   将API返回的数据模型转换为App内部使用的 `CardBalanceData` 模型。
    -   **位置**: `composeApp/src/commonMain/kotlin/cn/edu/ysu/ciallo/cardbalance/CardBalanceRepository.kt` (在此文件中新增实现)

### 变更计划

1.  **引入依赖**: 在 `build.gradle.kts` 中添加 Ktor (client, content-negotiation, serialization) 和 Ksoup (HTML解析) 的依赖。
2.  **创建API层**:
    -   实现 `YsuEhallApi.kt`，复现 Notebook 中的登录和数据获取逻辑。
    -   实现 `YsuEhallApiFactory.kt` 来管理API实例和凭据。
3.  **实现远程Repository**: 在 `CardBalanceRepository.kt` 中创建 `RemoteCardBalanceRepository` 类，通过 `YsuEhallApi` 获取数据。
4.  **更新ViewModel**: 修改 `App.kt` 中 `CardBalanceViewModel` 的初始化逻辑，使其能够方便地在 `Mock` 和 `Remote` 实现之间切换。
5.  **数据模型**: 创建 `YsuCardBalanceResponse.kt` 用于解析API返回的JSON数据。

### 优势

-   **松耦合**: UI层 (Compose) -> ViewModel -> Repository -> API层。每一层都只与相邻的层交互，API逻辑的变更不会影响到UI。
-   **可扩展性**: 未来需要增加新的API（如获取课表、成绩），只需在 `YsuEhallApi` 中增加新的方法，并创建对应的 `RemoteRepository` 实现即可，对现有代码影响极小。
-   **可测试性**: 依赖注入的设计使得我们可以轻松地为 `ViewModel` 提供 `MockRepository` 进行单元测试，也可以独立测试 `RemoteRepository` 和 `YsuEhallApi`。

---
如需补充具体页面、功能、资源等细节，请在本文件继续记录。
