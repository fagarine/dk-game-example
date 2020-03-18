package cn.laoshini.game.example.tank.constant;

/**
 * @author fagarine
 */
public class TankConstants {
    private TankConstants() {
    }

    public static final int SELF_PLAT = 1;

    public static final int GAME_ID = 102;

    public static final String GAME_NAME = "坦克小游戏";

    public static final String[] PACKAGES = { "cn.laoshini.game.example.tank" };

    public static final String[] HANDLER_PACKAGES = { "cn.laoshini.game.example.tank.handler" };

    public static final String[] MESSAGE_PACKAGES = { "cn.laoshini.game.example.tank.message" };

    public static final String[] ENTITY_PACKAGES = { "cn.laoshini.game.example.tank.entity" };

    public static final int INIT_PORTRAIT = 1;

    /** 房间X轴长度 */
    public static final int ROOM_X_LEN = 6400;

    /** 房间Y轴长度 */
    public static final int ROOM_Y_LEN = 3600;

    /** 房间边界宽度 */
    public static final int ROOM_BORDER = 50;

    /** 初始移动速度 */
    public static final int INIT_SPEED = 300;

    /** 初始坦克id */
    public static final int INIT_TANK_ID = 101;

    /** 初始子弹id */
    public static final int INIT_BULLET_ID = 1;

    /** 房间排行榜长度 */
    public static final int ROOM_RANK_SIZE = 10;

    /** 空房间失效时间，毫秒 */
    public static final int ROOM_INVALID_TIME = 3 * 60 * 1000;

    /** 普通击杀加分 */
    public static final int KILL_SCORE = 1;

    /** 初始复活等待时间 */
    public static final int INIT_RESURGENCE_DELAY = 3;

    // *********************消息ID相关常量 Begin**********************//

    /** 平台登陆相关消息id基础值 */
    public static final int PLAT_MESSAGE_HEAD = GAME_ID * 1000;

    /** 玩家消息id基础值 */
    public static final int MESSAGE_HEAD = PLAT_MESSAGE_HEAD + 100;

    /** 服务器推送到客户端的消息id基础值 */
    public static final int PUSH_HEAD = MESSAGE_HEAD + 500;

    /** GM消息id基础值 */
    public static final int GM_HEAD = PUSH_HEAD + 100;

    public static final int GET_UNIQUE_ID_REQ = 1;

    public static final int BEGIN_GAME_REQ = 1;

    public static final int GET_NAMES_REQ = 3;

    public static final int ROLE_LOGIN_REQ = 5;

    public static final int ROLE_LOGOUT_REQ = 7;

    public static final int HEART_BEAT_REQ = 9;

    public static final int GET_ROOMS_REQ = 11;

    public static final int ENTER_ROOM_REQ = 13;

    public static final int GET_ROOM_RANK_REQ = 15;

    public static final int TANK_MOVE_REQ = 17;

    public static final int TANK_SHOT_REQ = 19;

    public static final int EXIT_ROOM_REQ = 21;

    public static final int TANK_COLLIDE_REQ = 23;

    public static final int ENTER_ROOM_PUSH = 2;

    public static final int TANK_MOVE_PUSH = 4;

    public static final int TANK_SHOT_PUSH = 6;

    public static final int BULLET_BLAST_PUSH = 8;

    public static final int TANK_DISAPPEAR_PUSH = 10;

    public static final int PLAYER_DEAD_PUSH = 12;

    public static final int TANK_SCORE_CHANGE_PUSH = 14;

    public static final int ROOM_RANK_CHANGE_PUSH = 16;

    public static final int TANK_APPEAR_PUSH = 18;

    public static final int GET_MODULE_LIST_REQ = 1;

    public static final int RELOAD_MODULES_REQ = 3;

    public static final int REMOVE_MODULE_REQ = 5;

    public static final int DO_HOTFIX_REQ = 7;

    public static final int GET_GAME_SERVER_INFO_REQ = 9;

    public static final int PAUSE_GAME_SERVER_REQ = 11;

    public static final int RELEASE_GAME_SERVER_REQ = 13;

    public static final int GET_PLAYERS_REQ = 15;

    public static final int KICK_OUT_PLAYER_REQ = 17;

    // *********************消息ID相关常量 End**********************//

}
