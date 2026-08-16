# Moonlight Android TV 适配版

基于 Moonlight Android v12.1 制作的电视专用改造版本，面向 Android TV 智能电视、
电视盒子、投影仪、激光电视和其他主要依靠遥控器操作的大屏 Android 设备。

本版本重点解决部分厂商 TV 固件上的弹窗白屏、设置选项不显示、码率数值不可见、
应用封面过小、遥控器无法退出串流，以及退出后不能再次进入已有 Sunshine 会话等问题。

> 本项目是非官方改造版。Moonlight、Sunshine 及相关名称和图标归各自项目所有。

## 下载 APK

[**点击下载 Moonlight TV v12.1-tv.1 APK**](dist/moonlight-tv-v12.1-tv.1-debug.apk)

| 项目 | 信息 |
| --- | --- |
| 版本 | `v12.1-tv.1` |
| Android 包名 | `com.limelight.debug` |
| APK 类型 | Non-Root Debug 通用包 |
| 文件大小 | 约 9.69 MiB |
| 支持架构 | arm64-v8a、armeabi-v7a、x86、x86_64 |
| SHA-256 | `D377404AEB63CB8842E950B4837BDBC07BA644F774EF2B277A1D8BF94037E4A2` |

该版本可以和包名为 `com.limelight` 的官方 Moonlight 同时安装。

## 主要功能

### 修复 TV 白色空白弹窗

部分电视只能显示弹窗标题，分辨率、帧数、应用操作等内容区域完全空白。
本版本使用应用自行绘制的 TV 菜单替代有问题的系统菜单，明确设置文字颜色、背景和焦点状态。

- 显示“恢复串流、退出串流、查看详情、创建快捷方式、取消”等操作；
- 当前焦点显示为蓝色，未选中项目使用黑色文字；
- 支持遥控器方向键、确认键和返回键；
- 不再依赖电视厂商可能无法正确渲染的原生 ContextMenu。

### 修复设置选项不显示

分辨率、视频帧数、音频模式、视频编码、帧速调节和语言选择使用 TV 专用列表组件，
即使电视系统原生 `ListPreference` 无法显示内容，也能正常选择。

### 显示码率具体数值

码率及其他滑块弹窗会实时显示当前数值和单位，避免只能看到滑块却不知道实际码率。

### 恢复大尺寸应用封面

部分电视会被 Android 错误识别为小屏设备，从而启用 `100 × 133dp` 小封面。
本版本在电视环境中固定使用 `150 × 200dp` 大封面布局。

### 针对遥控器优化

- 遥控器返回键可以退出正在连接或已经连接的串流；
- 非游戏手柄来源的 B 键也可以作为返回键；
- 真正的游戏手柄按键仍会发送到远程电脑；
- 退出客户端不会主动关闭电脑上的 Sunshine 应用会话。

### 改善二次进入已有会话

TV 使用独立的 `TvGame` 串流页面，每次进入都会重新创建 Surface、解码器和连接生命周期。
新的连接会等待旧连接完全释放后再恢复 Sunshine 会话，减少白屏、卡住和无法二次进入的问题。

## 适用设备与场景

推荐用于：

- Android TV 智能电视；
- Android 电视盒子和运营商机顶盒；
- Android 投影仪、激光电视和会议大屏；
- 只能使用方向键、确认键和返回键操作的设备；
- 原生弹窗只有标题或白色内容区的电视固件；
- 应用封面被错误显示为小图标的电视；
- 使用 Sunshine，需要退出客户端后继续恢复电脑端会话的环境；
- 客厅局域网游戏、桌面远程控制和影音主机串流。

手机和平板通常建议继续使用 Moonlight 官方版本。

## 安装方法

1. 从本页面或 Releases 下载 APK；
2. 将 APK 复制到电视、U 盘或局域网共享；
3. 在电视设置中允许文件管理器“安装未知应用”；
4. 打开 APK 并完成安装。

也可以使用 ADB：

```bash
adb install -r moonlight-tv-v12.1-tv.1-debug.apk
```

## 建议测试流程

1. 打开串流设置，检查分辨率、帧数和码率数值；
2. 进入电脑应用列表，确认 Desktop、Steam 等封面使用大卡片；
3. 启动 Desktop 或 Steam；
4. 使用遥控器返回键退出串流，但保留电脑端应用运行；
5. 再次选择正在运行的应用；
6. 在中文操作菜单中选择“恢复串流”；
7. 测试“退出串流”确认窗口和返回键取消操作。

## 源码与详细说明

- [完整功能、适用场景和技术说明](TV_EDITION_README.zh-CN.md)
- [Moonlight Android 官方项目](https://github.com/moonlight-stream/moonlight-android)
- [Sunshine 官方项目](https://github.com/LizardByte/Sunshine)

## 从源码构建

安装 Android SDK、Android NDK 和 Java 环境，然后执行：

```bash
git submodule update --init --recursive
./gradlew :app:assembleNonRootDebug
```

输出文件位于：

```text
app/build/outputs/apk/nonRoot/debug/app-nonRoot-debug.apk
```

当前 APK 使用 compileSdk 34、NDK 27.0.12077973 和 Gradle 8.8 构建，
并通过 APK Signature Scheme v1/v2 校验。

## 开源许可

本项目继承 Moonlight Android 的 [GPL-3.0 License](LICENSE.txt)。修改版源码和 APK
按相同许可发布。正式版本和后续上游更新请以 Moonlight 官方项目为准。
