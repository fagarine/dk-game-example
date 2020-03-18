package cn.laoshini.game.example.tank.service;

import java.util.Collection;

import cn.laoshini.game.example.tank.common.TankException;
import cn.laoshini.game.example.tank.domain.RoomInfo;
import cn.laoshini.game.example.tank.domain.TankPlayer;
import cn.laoshini.game.example.tank.message.room.EnterRoomReq;
import cn.laoshini.game.example.tank.message.room.ExitRoomReq;
import cn.laoshini.game.example.tank.message.room.GetRoomRankReq;
import cn.laoshini.game.example.tank.message.room.TankCollideReq;
import cn.laoshini.game.example.tank.message.room.TankMoveReq;
import cn.laoshini.game.example.tank.message.room.TankShotReq;

/**
 * 房间相关服务接口
 *
 * @author fagarine
 */
public interface IRoomService {

    /**
     * 获取所有房间信息
     *
     * @return 返回所有房间信息，该方法不允许返回null
     * @throws TankException 统一异常声明
     */
    Collection<RoomInfo> getRooms() throws TankException;

    /**
     * 玩家进入房间
     *
     * @param req 客户端进入消息
     * @param player 玩家
     * @throws TankException 统一异常声明
     */
    void enterRoom(EnterRoomReq req, TankPlayer player) throws TankException;

    /**
     * 获取房间内排行榜
     *
     * @param req 客户端进入消息
     * @param player 玩家
     * @throws TankException 统一异常声明
     */
    void getRoomRank(GetRoomRankReq req, TankPlayer player) throws TankException;

    /**
     * 玩家移动
     *
     * @param req 客户端进入消息
     * @param player 玩家
     * @throws TankException 统一异常声明
     */
    void tankMove(TankMoveReq req, TankPlayer player) throws TankException;

    /**
     * 玩家射击
     *
     * @param req 客户端进入消息
     * @param player 玩家
     * @throws TankException 统一异常声明
     */
    void tankShot(TankShotReq req, TankPlayer player) throws TankException;

    /**
     * 玩家退出房间
     *
     * @param req 客户端进入消息
     * @param player 玩家
     * @throws TankException 统一异常声明
     */
    void exitRoom(ExitRoomReq req, TankPlayer player) throws TankException;

    /**
     * 客户端检测到碰撞，发送到服务端
     *
     * @param req 客户端进入消息
     * @param player 玩家
     * @throws TankException 统一异常声明
     */
    void tankCollide(TankCollideReq req, TankPlayer player) throws TankException;
}
