# AkkariinYSU 快速入门指南

欢迎来到 AkkariinYSU 项目！本指南旨在帮助你快速理解项目的设计理念、技术架构和代码结构，从而能够轻松地运行、调试和贡献代码。

## 1. 项目概述

AkkariinYSU 是一个非官方的、为燕山大学学生设计的第三方校园服务客户端。它旨在整合燕山大学网上服务大厅（E-hall）的各项功能，如查询校园卡余额、日程、图书借阅等，并在一个现代化的、统一的用户界面中展示它们。

**核心技术栈:**

*   **Kotlin Multiplatform (KMP)**: 项目的核心框架，允许业务逻辑和部分UI代码在多个平台间共享。
*   **Compose Multiplatform**: 用于构建用户界面，一套代码可以运行在 Android、桌面（JVM）和 Web (WasmJs) 上。
*   **Ktor Client**: 一个现代化的、异步的网络请求库，用于与学校的服务器进行通信。
*   **Kotlinx Serialization**: 用于解析服务器返回的 JSON 数据。
*   **MVVM 架构**: 应用遵循 Model-View-ViewModel 设计模式，以实现清晰的分层和关注点分离。

**目标平台:**

*   Android
*   桌面 (Windows, macOS, Linux)
*   Web (WebAssembly)

---

## 2. 项目结构

项目代码主要位于 `composeApp` 模块中，其结构遵循 Kotlin Multiplatform 的标准布局。

```
.
├── composeApp/
│   ├── src/
│   │   ├── commonMain/         # 核心共享代码 (UI, ViewModel, Repository, API)
│   │   │   ├── kotlin/
│   │   │   │   └── cn/edu/ysu/ciallo/
│   │   │   │       ├── components/ # 可复用的UI组件
│   │   │   │       ├── home/       # 首页功能模块 (View, ViewModel, Data)
│   │   │   │       ├── cardbalance/# 校园卡余额模块
│   │   │   │       ├── ysu/        # 与学校服务交互的核心API层
│   │   │   │       └── App.kt      # 应用主入口和导航
│   │   │   └── composeResources/   # 共享资源 (图片, 字体等)
│   │   ├── androidMain/        # Android 平台特定代码 (e.g., MainActivity)
│   │   ├── jvmMain/            # 桌面平台特定代码 (e.g., main.kt)
│   │   └── wasmJsMain/         # WebAssembly 平台特定代码
│   └── build.gradle.kts        # composeApp 模块的构建脚本
│
├── ysu_reverse/                # Python 脚本，用于逆向分析学校API（辅助开发）
├── build.gradle.kts            # 项目根构建脚本
└── settings.gradle.kts         # 项目设置脚本
```

*   **`commonMain`**: 这是项目的核心。绝大部分代码都在这里，包括所有业务逻辑、UI界面（Compose）、ViewModel、数据仓库和网络API客户端。
*   **`platformMain` (`androidMain`, `jvmMain`, `wasmJsMain`)**: 这些目录包含少量平台特定的“胶水”代码，主要用于初始化应用和实现平台特定的功能（例如，在不同平台上存储Cookie的文件路径不同）。
*   **`ysu_reverse`**: 包含一些用于辅助开发的 Python 脚本。当你需要分析学校网站的新接口或登录逻辑变更时，这些脚本会非常有用。

---

## 3. 架构设计 (MVVM)

应用采用经典的 **Model-View-ViewModel (MVVM)** 架构模式，确保代码的解耦、可测试性和可维护性。

**数据流:**

`View (UI) <--> ViewModel <--> Repository <--> API Client (Model)`

1.  **View (视图层)**
    *   **实现**: 使用 Compose Multiplatform 编写的Composable函数（例如 `HomePage.kt`）。
    *   **职责**: 只负责展示UI界面和将用户的操作（如点击、输入）通知给 ViewModel。它通过观察 ViewModel 中的状态（`StateFlow`）来更新自身。

2.  **ViewModel (视图模型)**
    *   **实现**: 普通的 Kotlin 类，继承自 `ViewModel` 或类似的生命周期感知组件（例如 `HomeViewModel.kt`）。
    *   **职责**:
        *   持有并管理与UI相关的状态（UI State），通常使用 `StateFlow` 或 `State` 来实现。
        *   响应来自 View 的用户事件，调用 Repository 来执行业务逻辑（如获取数据）。
        *   将 Repository 返回的数据转换成可供UI展示的状态。

3.  **Repository (数据仓库)**
    *   **实现**: 一个接口 (`HomeRepository`) 和它的多个实现 (`FakeHomeRepository`, `RemoteHomeRepository`)。
    *   **职责**:
        *   作为统一的数据来源入口，为 ViewModel 提供数据。
        *   屏蔽数据的具体来源。ViewModel 无需关心数据是从网络API获取、本地数据库读取还是来自一个假的实现。这极大地便利了UI的开发和测试。

4.  **Model (模型层 / API层)**
    *   **实现**: `YsuEhallApi.kt` 和相关的数据类（如 `HomeData.kt`, `CardBalanceResponse.kt`）。
    *   **职责**:
        *   **数据类**: 定义应用的数据结构。
        *   **API Client**: 负责所有网络相关的操作，包括构造请求、发送请求、处理响应和解析数据。这是与学校服务器直接交互的唯一模块。

---

## 4. 核心模块详解

### `ysu` 包：与学校服务的接口

这是整个应用最核心、最复杂的部分，位于 `composeApp/src/commonMain/kotlin/cn/edu/ysu/ciallo/ysu/`。

**`YsuEhallApi.kt`** 是所有魔法发生的地方。由于学校没有提供官方的API，这个类通过 **模拟浏览器行为** 来获取数据：

1.  **模拟登录**:
    *   它首先用 Ktor 请求登录页面。
    *   然后用 `ksoup` 解析HTML，提取出动态生成的安全令牌（`lt`, `execution`）和用于加密密码的 `salt`。
    *   使用 `PasswordEncryptor.kt` 中定义的算法（模仿浏览器中的JavaScript）加密用户密码。
    *   最后，将所有参数打包，提交登录表单。
2.  **会话保持**:
    *   登录成功后，服务器会返回一个 Session Cookie。
    *   `CustomCookieStorage.kt` 负责将这个 Cookie 保存到设备磁盘上，以便下次启动应用时可以自动登录。
3.  **数据获取**:
    *   所有后续的请求（如获取用户信息、查询余额）都会携带这个 Cookie，从而通过服务器的身份验证。

> **重要提示**: 这个模块的实现非常脆弱。一旦学校更新其登录页面的前端代码或后端逻辑，这里的代码很可能会失效，需要重新进行逆向分析和适配。

### `home` 包：一个典型的功能模块

`home` 包是理解 MVVM 实践的绝佳范例。

*   **`HomePage.kt` (View)**: 包含 `HomePage` Composable 函数，负责构建首页的UI。它从 `HomeViewModel` 中获取数据并展示。
*   **`HomeViewModel.kt` (ViewModel)**: 管理首页的状态，例如加载状态、用户信息、校园卡信息等。它通过调用 `HomeRepository` 来获取这些数据。
*   **`HomeData.kt` (Model/Repository)**:
    *   定义了 `HomeData` 数据类，这是首页所需所有数据的集合。
    *   定义了 `HomeRepository` 接口。
    *   提供了 `RemoteHomeRepository`，它会调用 `YsuEhallApi` 来获取真实数据。
    *   提供了 `FakeHomeRepository`，它直接返回一个写死的 `DEFAULT_HOME_DATA`。在开发UI或无法连接到学校网络时，这非常有用。

---

## 5. 如何快速上手

### 环境准备

1.  安装最新版的 [IntelliJ IDEA](https://www.jetbrains.com/idea/) 或 [Android Studio](https://developer.android.com/studio)。
2.  确保已安装 JDK 17 或更高版本。

### 运行项目

你可以通过 IntelliJ IDEA/Android Studio 的运行配置来启动不同平台的应用：

*   **`composeApp`**: 运行 Android 应用（需要连接安卓设备或启动模拟器）。
*   **`Run-Desktop`**: 运行桌面版应用。
*   **`Run-Wasm`**: 运行 WebAssembly 版本。

### 开发与调试技巧

*   **使用假数据进行UI开发**:
    在 `HomeViewModel.kt` 中，你可以将 `HomeRepository` 的实现从 `RemoteHomeRepository` 切换到 `FakeHomeRepository`。
    ```kotlin
    // In HomeViewModel.kt
    // private val repository: HomeRepository = RemoteHomeRepository()
    private val repository: HomeRepository = FakeHomeRepository() // Use fake data
    ```
    这样，你就可以在不进行网络请求、甚至不需要输入账号密码的情况下，快速调试和迭代UI。

*   **分析网络请求**:
    `YsuEhallApi.kt` 中已经配置了 Ktor 的日志插件。在运行应用时，你可以在控制台（Logcat 或 Run 窗口）看到详细的网络请求和响应日志，这对于调试API问题至关重要。

祝你编码愉快！
