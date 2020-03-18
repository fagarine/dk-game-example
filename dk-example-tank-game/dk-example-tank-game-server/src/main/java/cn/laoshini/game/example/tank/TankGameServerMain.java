package cn.laoshini.game.example.tank;

import io.netty.util.Attribute;

import cn.laoshini.dk.annotation.Message;
import cn.laoshini.dk.common.SpringContextHolder;
import cn.laoshini.dk.domain.msg.AbstractMessage;
import cn.laoshini.dk.domain.msg.ReqMessage;
import cn.laoshini.dk.domain.msg.RespMessage;
import cn.laoshini.dk.executor.OrderedQueuePoolExecutor;
import cn.laoshini.dk.gm.GmHttpRequestUriInterceptor;
import cn.laoshini.dk.net.MessageHandlerHolder;
import cn.laoshini.dk.net.codec.JsonNettyMessageDecoder;
import cn.laoshini.dk.net.codec.JsonTextNettyMessageEncoder;
import cn.laoshini.dk.net.session.NettyHttpSession;
import cn.laoshini.dk.net.session.NettyWebsocketSession;
import cn.laoshini.dk.register.AbstractGameDataLoaderAdaptor;
import cn.laoshini.dk.register.ClassFilters;
import cn.laoshini.dk.register.ClassIdReader;
import cn.laoshini.dk.register.GmServerRegister;
import cn.laoshini.dk.register.IEntityRegister;
import cn.laoshini.dk.register.IGameDataLoader;
import cn.laoshini.dk.register.IGameServerRegister;
import cn.laoshini.dk.register.IMessageHandlerRegister;
import cn.laoshini.dk.register.IMessageRegister;
import cn.laoshini.dk.register.Registers;
import cn.laoshini.dk.register.WebsocketGameServerRegister;
import cn.laoshini.dk.starter.DangKangGameStarter;
import cn.laoshini.dk.util.LogUtil;
import cn.laoshini.game.example.tank.annotation.TankMessage;
import cn.laoshini.game.example.tank.common.ChannelAttr;
import cn.laoshini.game.example.tank.config.TankConfig;
import cn.laoshini.game.example.tank.config.TankRobotConfig;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.manage.bullet.BulletDataManager;
import cn.laoshini.game.example.tank.manage.ini.StaticDataManager;
import cn.laoshini.game.example.tank.manage.player.PlayerDataManager;
import cn.laoshini.game.example.tank.manage.robot.TankRobotMachine;
import cn.laoshini.game.example.tank.manage.room.RoomDataManager;
import cn.laoshini.game.example.tank.manage.util.TimerTaskUtil;
import cn.laoshini.game.example.tank.manage.worker.ReceivedMessageWorker;
import cn.laoshini.game.example.tank.service.IPlayerService;

/**
 * @author fagarine
 */
public class TankGameServerMain {

    private static OrderedQueuePoolExecutor MESSAGE_EXECUTOR;

    public static void main(String[] args) {
        DangKangGameStarter.get()
                // 如果不使用配置中心，由于项目中使用了dk-basic-da中的组件，会需要用到数据库连接相关配置，需要通过这种方法提前引入配置文件
                //                .propertyConfigs("config.properties")
                // 使用配置中心，且配置中心客户端配置文件使用自定义文件名（配置中心客户端依赖于配置中心服务）
                .configClientFile("client.properties")
                // 通过上面的方式已经引入了项目的配置项文件，则Spring配置文件中不需要再引入该配置文件
                .springConfigs("applicationContext.xml").packagePrefixes(TankConstants.PACKAGES)
                .gameServer(tankGameServerRegister())
                // 数据库实体类注册
                .entity(tankEntityRegister())
                // 消息类注册
                .message(tankMessageRegister())
                // 消息处理类注册
                .messageHandler(tankMessageHandlerRegister())
                // GM相关消息，这里使用cn.laoshini.dk:dk-gm模块实现的基本GM功能
                .message(tankGmMessageRegister()).messageHandler(tankGmMessageHandlerRegister())
                // 开始启动
                .start();
    }

    private static IGameServerRegister<NettyWebsocketSession, AbstractMessage<?>> tankGameServerRegister() {
        WebsocketGameServerRegister<NettyWebsocketSession, AbstractMessage<?>> register = Registers
                .newWebsocketGameServerRegister();
        register.setGameId(TankConstants.GAME_ID).setGameName(TankConstants.GAME_NAME)
                // 通过配置项参数设置端口号
                .setPortByProperty("tank.game.port")
                // 设置消息解码器，JsonNettyMessageDecoder会自动识别字符串类型或二进制类型json消息
                .setMessageDecode(new JsonNettyMessageDecoder())
                // 设置消息编码器，数据格式使用字符串
                .setMessageEncode(new JsonTextNettyMessageEncoder())
                // 连接建立时的处理逻辑，将客户端连接与玩家对象关联
                .onConnected(session -> {
                    Attribute<TankPlayer> attribute = session.getChannel().attr(ChannelAttr.PLAYER);
                    TankPlayer player = new TankPlayer();
                    attribute.set(player);
                    session.setSubject(player);
                    player.setSession(session);
                })
                // 连接断开时的处理逻辑
                .onDisconnected(session -> {
                    SpringContextHolder.getBean(IPlayerService.class).roleLogout((TankPlayer) session.getSubject());
                })
                // 消息到达时的处理逻辑，添加消息到任务队列
                .onMessageDispatcher((session, message) -> {
                    MESSAGE_EXECUTOR.addTask(session.getId(),
                            new ReceivedMessageWorker((ReqMessage) message, (TankPlayer) session.getSubject()));
                })
                // 添加游戏数据加载器，可添加多个
                .addDataLoader(staticDataLoader()).addDataLoader(playerDataLoader())
                // 游戏服线程启动成功后的任务逻辑
                .onServerStarted(TankGameServerMain::serverStartedHandle);

        // GM服务器注册，GM服务默认使用HTTP协议通信
        GmServerRegister<NettyHttpSession, AbstractMessage<?>> gmRegister = GmServerRegister.create();
        gmRegister.setPortByProperty("tank.gm.port").setGameId(TankConstants.GAME_ID)
                .setGameName(TankConstants.GAME_NAME + "GM服务").setMessageDecode(new JsonNettyMessageDecoder())
                .setMessageEncode(new JsonTextNettyMessageEncoder())
                // GM请求URI拦截器，拦截所有不是指定URI的请求
                .addMessageInterceptor(GmHttpRequestUriInterceptor.of("/tank/gm.do"))
                // 消息到达处理，后台消息，不需要严格的按顺序执行，交给线程池处理
                .onMessageDispatcher((session, message) -> {
                    TimerTaskUtil.execute(() -> {
                        try {
                            // 由于是GM服务器，接受来自后台服务器的消息，没有玩家概念，所以没有GameSubject对象
                            RespMessage resp = MessageHandlerHolder.doMessageHandlerCall((ReqMessage) message, null);
                            session.sendMessage(resp);
                        } catch (Exception e) {
                            LogUtil.error("后台消息处理出错:" + message, e);
                        }
                    });
                }).onServerStarted(() -> LogUtil.start("GM服务器启动成功"));
        gmRegister
                // 添加受管的游戏服id
                .addManagedServerId(register::serverId)
                // 设置游戏服暂停对外服务时，GM后台消息过滤器
                .setConsoleMessageFilter(msg -> {
                    // 游戏服停止服务时，只有GM消息可以继续传入
                    TankMessage message = msg.getClass().getAnnotation(TankMessage.class);
                    return message != null && message.gm();
                });

        return register.setGmServerRegister(gmRegister);
    }

    private static IMessageRegister tankMessageRegister() {
        return Registers
                .newMessageRegister(ClassFilters.newAnnotationFilter(TankMessage.class), TankConstants.MESSAGE_PACKAGES,
                        ClassIdReader.annotationReader(TankMessage.class, "id"));
    }

    private static IMessageRegister tankGmMessageRegister() {
        return Registers.newMessageRegister(ClassFilters.newAnnotationFilter(Message.class),
                new String[] { "cn.laoshini.dk.gm.message" }, ClassIdReader.annotationReader(Message.class, "id"));
    }

    private static IMessageHandlerRegister tankMessageHandlerRegister() {
        return Registers.newDangKangHandlerRegister(TankConstants.HANDLER_PACKAGES)
                // 设置成单例模式，则handler中的Spring组件依赖会由系统自动注入
                .singleton();
    }

    private static IMessageHandlerRegister tankGmMessageHandlerRegister() {
        return Registers.newDangKangHandlerRegister(new String[] { "cn.laoshini.dk.gm.handler" })
                // 设置成单例模式，则handler中的Spring组件依赖会由系统自动注入
                .singleton();
    }

    private static IEntityRegister tankEntityRegister() {
        return Registers.newDangKangEntityRegister(TankConstants.ENTITY_PACKAGES);
    }

    private static IGameDataLoader playerDataLoader() {
        return new AbstractGameDataLoaderAdaptor("玩家数据加载") {
            @Override
            public void load() {
                PlayerDataManager playerDataManager = SpringContextHolder.getBean(PlayerDataManager.class);
                playerDataManager.loadAllPlayer();
            }

            @Override
            public void reload() {
                // 玩家数据暂不实现热加载
            }
        };
    }

    private static IGameDataLoader staticDataLoader() {
        return new AbstractGameDataLoaderAdaptor("配置数据加载") {
            @Override
            public void load() {
                StaticDataManager staticDataManager = SpringContextHolder.getBean(StaticDataManager.class);
                staticDataManager.load();
            }

            @Override
            public void reload() {
                load();
            }
        };
    }

    private static void serverStartedHandle() {
        TankConfig tankConfig = SpringContextHolder.getBean(TankConfig.class);
        // 启动到达消息的处理线程池
        MESSAGE_EXECUTOR = new OrderedQueuePoolExecutor("tank-received-message", tankConfig.getReceiveMessageThreads());

        // 添加玩家移动定时任务
        PlayerDataManager playerDataManager = SpringContextHolder.getBean(PlayerDataManager.class);
        TimerTaskUtil.fixedRateTask(playerDataManager::playerMoveTimerLogic, 1000, 200);

        // 添加子弹定时任务
        BulletDataManager bulletDataManager = SpringContextHolder.getBean(BulletDataManager.class);
        TimerTaskUtil.fixedRateTask(bulletDataManager::bulletMoveTimerLogic, 1000, 200);

        // 添加房间定时任务
        RoomDataManager roomDataManager = SpringContextHolder.getBean(RoomDataManager.class);
        TimerTaskUtil.fixedRateTask(roomDataManager::roomTimerLogic, 1000, 5000);

        // 添加机器人定时任务
        TankRobotConfig robotConfig = SpringContextHolder.getBean(TankRobotConfig.class);
        if (robotConfig.isRobotOpen()) {
            TankRobotMachine tankRobotMachine = SpringContextHolder.getBean(TankRobotMachine.class);
            tankRobotMachine.initialize();
            TimerTaskUtil.fixedDelayTask(tankRobotMachine::tick, 1000, 500);
        }
    }
}
