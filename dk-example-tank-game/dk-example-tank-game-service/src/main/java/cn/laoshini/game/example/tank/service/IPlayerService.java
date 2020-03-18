package cn.laoshini.game.example.tank.service;

import cn.laoshini.game.example.tank.common.TankException;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.message.BeginGameReq;
import cn.laoshini.game.example.tank.message.HeartBeatReq;
import cn.laoshini.game.example.tank.message.RoleLoginReq;

/**
 * 玩家相关服务接口
 *
 * @author fagarine
 */
public interface IPlayerService {

    /**
     * 获取一个唯一ID
     *
     * @return 返回用户唯一id
     * @throws TankException 统一异常声明
     */
    String getUniqueId() throws TankException;

    /**
     * 客户端开始游戏，登录前必须走的流程
     *
     * @param req 客户端进入消息
     * @param player 玩家
     * @throws TankException 统一异常声明
     */
    void beginGame(BeginGameReq req, TankPlayer player) throws TankException;

    /**
     * 随机一些可用名称
     *
     * @param player 玩家
     * @throws TankException 统一异常声明
     */
    void getNames(TankPlayer player) throws TankException;

    /**
     * 玩家登录
     *
     * @param req 客户端进入消息
     * @param player 玩家
     * @throws TankException 统一异常声明
     */
    void roleLogin(RoleLoginReq req, TankPlayer player) throws TankException;

    /**
     * 玩家登出
     *
     * @param player 玩家
     * @throws TankException 统一异常声明
     */
    void roleLogout(TankPlayer player) throws TankException;

    /**
     * 心跳消息处理
     *
     * @param req 客户端进入消息
     * @param player 玩家
     * @throws TankException 统一异常声明
     */
    void heartBeat(HeartBeatReq req, TankPlayer player) throws TankException;
}
