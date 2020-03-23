## 项目介绍
该项目是为了引导用户快速使用当康系统的示例项目，当康系统项目链接：
- [github链接](https://github.com/fagarine/dangkang)
- [码云链接](https://gitee.com/fagarine/dangkang)

### 项目结构
```text
dk-game-example
├── dk-example-chat-game -- 简单的聊天游戏
├── dk-example-chat-game-starter -- 简单的聊天游戏，通过配置项启动服务
├── dk-example-chat-protobuf -- 简单的聊天游戏Protobuf版，使用protobuf通信
└── dk-example-tank-game -- 简单的坦克战斗小游戏
    ├── dk-example-tank-game-common
    ├── dk-example-tank-game-domain
    ├── dk-example-tank-game-manager
    ├── dk-example-tank-game-service
    ├── dk-example-tank-game-hall -- 游戏大厅逻辑
    ├── dk-example-tank-game-room -- 游戏房间内逻辑
    └── dk-example-tank-game-server -- 服务器启动模块
```

### 示例项目启动说明
- dk-example-chat-game和dk-example-chat-protobuf的客户端都是使用javafx实现，执行客户端程序启动类的main方法就可以启动
- dk-example-tank-game项目的客户端代码没有包含在项目中，想要看运行效果，[点击这里进入](http://game.laoshini.cn/game/tt/)，建议在手机上查看

### 坦克游戏项目说明
一般而言，这样的小项目没有必要拆分成7个模块，之所以拆分的原因：
- 各模块职责明确，易于扩展
- 业务逻辑和数据分离
- 打包时分开打包，可以使用当康项目的外置模块功能，不停服热更新代码

该项目中引入了配置中心客户端，服务启动时，会自动从配置中心拉取配置信息。
该功能依赖配置中心服务，用户需要先启动配置中心，添加游戏服配置，并将配置中心URL添加到[配置中心客户端配置文件](/dk-example-tank-game/dk-example-tank-game-server/src/main/resources/client.properties)中。

#### 该项目用到的当康系统功能
- 游戏服快速启动，详见[游戏启动类](/dk-example-tank-game/dk-example-tank-game-server/src/main/java/cn/laoshini/game/example/tank/TankGameServerMain.java)
- 数据库访问支持
- 数据库实现的ID自增器
- 中文姓名生成器
- 配置中心客户端
- 进程内机器人，有限状态机
- GM功能支持

### 项目链接
- [github链接](https://github.com/fagarine/dk-game-example)
- [码云链接](https://gitee.com/fagarine/dk-game-example)

### 运行结果展示
聊天游戏界面展示
![chat-screen](https://s1.ax1x.com/2020/03/23/8T8uS1.png)

坦克游戏界面展示
![tank-screen](https://s1.ax1x.com/2020/03/23/8TYd61.jpg)