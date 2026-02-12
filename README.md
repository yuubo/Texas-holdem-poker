# Texas Hold'em Poker（德州扑克 - 控制台局域网版）

本项目是一个用 Java 编写的德州扑克（Texas Hold'em Poker）游戏，支持在局域网环境下通过控制台进行对战。

## 项目简介

- **语言**：Java
- **使用场景**：适用于局域网（LAN）环境下，玩家通过控制台进行德州扑克游戏。
- **面向对象设计**：核心代码采用面向对象思路，实现清晰的牌桌、玩家、发牌、下注等模块。
- **玩法贴合经典德州规则**：包括发牌、下注、比牌、胜负结算等完整流程。

## 主要功能

- 支持多玩家通过局域网连接
- 控制台界面下的文字交互
- 德州扑克基本玩法与规则
- 洗牌、发牌、下注、比牌、结算等逻辑完整
- 代码易于扩展，可按需加入更多玩法或网络协议

## 快速开始

1. **克隆项目**
    ```shell
    git clone https://github.com/yuubo/Texas-holdem-poker.git
    ```
2. **编译项目**
    ```shell
    javac -d bin src/*.java
    ```
3. **运行服务器**
    ```shell
    java -cp bin PokerServer
    ```
4. **运行客户端（每个玩家各启动一次）**
    ```shell
    java -cp bin PokerClient
    ```

## 文件结构示例

```
src/
├── PokerServer.java      // 服务端主程序文件
├── PokerClient.java      // 客户端主程序文件
├── Game.java             // 游戏主逻辑
├── Player.java           // 玩家实体
├── Poker.java            // 牌型/德州扑克核心逻辑
└── ...                   // 其他辅助类
```

## 玩法简单说明

1. 启动服务端（PokerServer）。
2. 各玩家运行客户端（PokerClient）并输入服务器 IP，实现多人联机。
3. 按照控制台提示进行下注、比牌等操作。

## 注意事项

- 请确保所有玩家与服务器处于同一局域网环境。
- 需要安装 Java 8 及以上版本。

## 贡献

欢迎提交 Issue 和 PR，并积极提出建议和反馈！

## 授权协议

本项目基于 MIT License 开源。

---

由 [yuubo](https://github.com/yuubo) 开发和维护。
