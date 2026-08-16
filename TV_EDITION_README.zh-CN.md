# Moonlight Android TV 改造版

这是基于开源项目 [Moonlight Android](https://github.com/moonlight-stream/moonlight-android)
v12.1 制作的 Android TV 适配版本，主要解决部分电视、电视盒子和投影仪固件对
Android 原生弹窗、焦点和串流 Activity 生命周期支持不完整的问题。

本项目不是 Moonlight 官方发行版。Moonlight、Sunshine 及相关名称和图标归各自项目所有。

## APK 下载

- [下载 Moonlight TV v12.1-tv.1 Debug APK](dist/moonlight-tv-v12.1-tv.1-debug.apk)
- Android 包名：`com.limelight.debug`
- 版本：`12.1`（versionCode `314`）
- 文件大小：约 9.69 MiB
- SHA-256：`D377404AEB63CB8842E950B4837BDBC07BA644F774EF2B277A1D8BF94037E4A2`
- ABI：`arm64-v8a`、`armeabi-v7a`、`x86`、`x86_64`

这是 Debug 签名版本，能够和包名为 `com.limelight` 的官方 Moonlight 同时安装。
它不能直接覆盖官方版本，但可以覆盖本仓库此前使用相同 Debug 签名生成的测试版。

## 解决的问题

### 1. TV 弹窗只有标题，选项区域完全空白

部分电视固件会创建 Android 原生 `ContextMenu` 或 `ListPreference` 弹窗，但只绘制白色背景和标题，
菜单文字、单选项或焦点层没有显示。用户看不到“恢复串流”“退出串流”、分辨率、帧数等选项。

本版本为 TV 提供完全自绘的操作菜单和列表选择窗口：

- 明确使用黑色文字和浅色背景；
- 当前焦点使用蓝色高亮和白色文字；
- 每个操作项都可以通过遥控器方向键聚焦；
- 确认键执行操作，返回键关闭弹窗；
- 不再依赖电视厂商可能损坏的原生 ContextMenu 内容布局。

应用操作菜单包含：

- 恢复已有串流；
- 退出电脑端当前会话；
- 退出当前应用并启动另一个应用；
- 查看应用详情；
- 创建桌面快捷方式；
- 隐藏应用；
- 取消并返回应用列表。

### 2. 分辨率、帧数等设置项无法显示

分辨率、视频帧数、音频模式、视频编码格式、帧速调节和语言选择改用 TV 专用列表组件。
选项由应用自身绘制，不再依赖电视系统的原生单选列表。

### 3. 码率滑块不显示数值

码率及其他滑块弹窗会使用固定的可见文字颜色，并在拖动时实时显示当前值和单位，
避免出现只能看到滑块、却不知道具体码率的问题。

### 4. 应用封面突然变小

部分电视没有正确声明 `FEATURE_TELEVISION`，Moonlight 会把它误判为小屏 Android 设备，
从而启用 `100 × 133dp` 的小封面布局。

本版本同时检查 Leanback、Television Feature 和 `UI_MODE_TYPE_TELEVISION`，
TV 环境固定使用原版 `150 × 200dp` 大封面布局。

### 5. 遥控器无法方便地退出串流

TV 遥控器的返回键和非游戏手柄来源的 B 键会优先退出当前串流页面，
不会被继续转发到远程电脑。真正的游戏手柄按键仍然保留给远程游戏使用。

退出客户端串流不会主动结束电脑上的 Sunshine 应用会话，稍后仍可选择同一个应用继续连接。

### 6. 退出后再次进入已有会话可能卡住

手机端原版 `Game` Activity 使用 `singleTask`。某些 TV 在旧 Surface 和解码器已经销毁后，
仍会复用旧 Activity，导致再次进入时出现空白页面或无法重新初始化连接。

本版本增加独立的 `TvGame` Activity：

- TV 每次选择串流都会获得新的 Surface、解码器和页面生命周期；
- Sunshine 的 resume 请求与 Moonlight 原生连接初始化被完整串行化；
- 新连接会等待旧连接释放完成，避免两个 Activity 同时恢复同一会话；
- Surface 尚未输出首帧时使用黑色背景，避免暴露白色系统窗口。

## 适用场景

本版本主要适合：

- Android TV 智能电视；
- 使用 Android TV 或厂商定制 Android 系统的电视盒子；
- Android 投影仪、激光电视和会议大屏；
- 只能使用方向键、确认键和返回键操作的设备；
- 原生弹窗只显示标题或白色内容区的电视固件；
- 被错误识别为手机界面、应用封面过小的电视设备；
- 使用 Sunshine，在退出客户端后需要继续恢复电脑端已有会话的场景；
- 客厅局域网游戏、桌面远程控制和影音主机串流。

## 不建议的场景

- 手机和平板通常不需要此版本，官方 Moonlight 的交互更符合触摸操作；
- 对安装包签名、自动更新或应用商店更新有要求时，应使用官方发行版；
- 公网串流仍需要正确配置 Sunshine、端口、防火墙或 VPN，本版本不会自动解决服务端网络问题；
- 不同电视厂商对焦点、返回键和硬件解码器的实现差异很大，无法保证覆盖所有型号。

## 安装方法

### 在电视上安装

1. 下载 `dist/moonlight-tv-v12.1-tv.1-debug.apk`；
2. 将 APK 复制到 U 盘、局域网共享或电视文件管理器；
3. 在电视系统中允许文件管理器“安装未知应用”；
4. 打开 APK 并完成安装。

### 使用 ADB 安装

```bash
adb install -r dist/moonlight-tv-v12.1-tv.1-debug.apk
```

如果设备上安装的是官方 `com.limelight`，本版本会作为单独应用存在。

## 建议测试流程

1. 打开串流设置，分别检查分辨率、帧数和码率是否显示；
2. 进入电脑应用列表，确认 Desktop、Steam 等封面使用大卡片；
3. 启动 Desktop 或 Steam；
4. 按遥控器返回键退出串流，但不要在电脑端关闭应用；
5. 再次点击正在运行的应用；
6. 检查自绘菜单是否显示“恢复串流”和“退出串流”；
7. 选择“恢复串流”，确认能够重新进入电脑画面；
8. 测试“退出串流”的二次确认窗口及返回键取消操作。

## 主要代码结构

| 模块 | 作用 |
| --- | --- |
| `TvUtils` | 统一识别 Android TV、Leanback 和电视 UI Mode |
| `TvActionDialog` | 自绘应用操作菜单、确认窗口和遥控器焦点状态 |
| `TvListPreference` | 自绘分辨率、帧数、编码等列表选择项 |
| `TvGame` | TV 专用的全新串流 Activity 生命周期 |
| `AppView` | TV 菜单路由、操作回调和大封面策略 |
| `NvConnection` | 串行化会话恢复、原生连接启动和停止清理 |
| `SeekBarPreference` | 实时显示码率等滑块数值与单位 |

## 从源码构建

项目需要 Android SDK、Android NDK 和 Java 环境。初始化子模块后执行：

```bash
git submodule update --init --recursive
./gradlew :app:assembleNonRootDebug
```

构建结果位于：

```text
app/build/outputs/apk/nonRoot/debug/app-nonRoot-debug.apk
```

本次提供的 APK 使用 compileSdk 34、NDK 27.0.12077973 和 Gradle 8.8 完成构建，
并通过 APK Signature Scheme v1/v2 校验。

## 反馈问题时请提供

- 电视、盒子或投影仪品牌和具体型号；
- Android 版本；
- Sunshine 版本；
- 连接方式（有线、Wi-Fi 5、Wi-Fi 6 或公网）；
- 问题出现前后的操作步骤；
- 弹窗或画面照片；
- 如果可以，附上 `adb logcat` 日志。

## 开源许可

本项目继承 Moonlight Android 的 [GPL-3.0 License](LICENSE.txt)。修改版源代码与 APK
按相同许可提供。上游项目及后续正式版本请以
[moonlight-stream/moonlight-android](https://github.com/moonlight-stream/moonlight-android) 为准。
