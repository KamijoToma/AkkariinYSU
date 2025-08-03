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
        - `composeResources/`：Compose资源
      - `androidMain/`：Android专用代码与资源
      - `jvmMain/`：JVM专用代码
      - `wasmJsMain/`：WASM/JS专用代码与资源
    - `build/`：编译输出
- `demo/`：演示图片等

## 主要功能模块
- App.kt：应用入口，包含底部导航与页面切换
- HomePage：主页，展示日程、电费、借书、图书馆、卡余额、校园网等信息卡片
- ClubRecommendPage：社团推荐页，展示社团列表

## 组件与页面结构
- 使用`Scaffold`实现整体布局，`NavigationBar`实现底部导航
- 各功能区块用`Card`+`Column`+`Row`组合
- 列表用`LazyColumn`实现
- 图标用`Icon`（可后续替换为自定义资源）

## 约定
- 业务逻辑与UI分离，后续可扩展ViewModel等架构
- 资源与多平台适配可在各`*Main`目录下实现

---
如需补充具体页面、功能、资源等细节，请在本文件继续记录。
