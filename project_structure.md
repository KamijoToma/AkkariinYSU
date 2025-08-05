# 项目结构与技术说明

## 技术栈
- **Kotlin Multiplatform (KMP)**: 支持多平台开发。
- **Jetpack Compose Multiplatform (JCM)**: 用于构建跨平台的现代化UI。
- **Compose Material3**: 提供现代化的UI组件。
- **Ktor**: 用于网络请求和API交互。
- **Kotlinx Serialization**: 用于JSON解析。

## 项目目录结构
- **根目录**
  - `build.gradle.kts`、`settings.gradle.kts`: 项目配置文件。
  - `composeApp/`: 主模块。
    - `src/`
      - `commonMain/`: 共享代码。
        - `kotlin/`: 包含业务逻辑和UI代码。
          - `components/`: 可复用的UI组件。
          - `home/`: 主页相关模块。
          - `cardbalance/`: 卡余额相关模块。
          - `ysu/`: 远程API对接层。
        - `composeResources/`: Compose资源。
      - `androidMain/`: Android平台专用代码。
      - `jvmMain/`: JVM平台专用代码。
      - `wasmJsMain/`: WASM/JS平台专用代码。
    - `build/`: 编译输出。
- `demo/`: 演示图片等。
- `commonTest/`: 单元测试代码。

## 核心功能模块

### 主页模块
- **文件位置**: `composeApp/src/commonMain/kotlin/cn/edu/ysu/ciallo/home/HomePage.kt`
- **功能**:
  - 提供底部导航和页面切换。
  - 组合各功能卡片组件。

### 卡余额模块
- **文件位置**: `composeApp/src/commonMain/kotlin/cn/edu/ysu/ciallo/cardbalance/`
- **功能**:
  - 显示卡余额信息。
  - 支持本地模拟和真实API切换。
  - 错误处理完善。

### 登录功能
- **文件位置**: `composeApp/src/commonMain/kotlin/cn/edu/ysu/ciallo/home/LoginPage.kt`
- **功能**:
  - 提供用户名和密码输入框。
  - 提供登录按钮，调用 `YsuEhallApi` 的 `login` 方法。
  - 显示登录状态（成功或失败）。

### 远程API对接层
- **文件位置**: `composeApp/src/commonMain/kotlin/cn/edu/ysu/ciallo/ysu/`
- **核心组件**:
  1. **`YsuEhallApi`**: 封装与E-Hall系统交互的网络请求逻辑。
     - **职责**:
       - 管理 `HttpClient` 实例。
       - 实现登录流程和业务接口调用。
       - 定义登录结果和UI状态。
     - **位置**: `YsuEhallApi.kt`
  2. **`YsuEhallApiFactory`**: 提供 `YsuEhallApi` 的单例实例。
     - **职责**:
       - 管理用户凭据。
       - 确保应用内共享同一登录会话。
     - **位置**: `YsuEhallApiFactory.kt`

### 示例流程
1. 用户在 `HomePage` 点击登录按钮，跳转到 `LoginPage`。
2. 用户输入用户名和密码，点击登录。
3. 登录成功后，返回 `HomePage`，并刷新各组件数据。
4. 如果登录失败，显示错误信息并允许重试。

## 数据层与状态管理
- **ViewModel**: 负责数据加载与状态管理。
- **Repository接口**: 提供本地假数据和远程API的切换。
- **数据模型**: 定义各模块的数据结构。
- **Cookie管理**: 统一管理API登录态。

## 组件拆分与复用
- **组件设计**:
  - 每个功能区块拆分为独立的Composable组件。
  - 页面级组件负责组合和布局功能组件。
- **拆分示例**:
  - `CardBalanceCard.kt`: 负责UI展示。
  - `CardBalanceViewModel.kt`: 负责数据加载和状态管理。
  - `HomePage.kt`: 组合各功能组件。

## 优势
- **松耦合**: UI层与数据层分离。
- **可扩展性**: 便于增加新功能。
- **可测试性**: 支持单元测试和Mock数据调试。

---
如需补充具体页面、功能、资源等细节，请在本文件继续记录。
