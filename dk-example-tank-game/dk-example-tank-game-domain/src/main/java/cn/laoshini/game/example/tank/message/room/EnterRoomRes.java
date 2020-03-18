package cn.laoshini.game.example.tank.message.room;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.laoshini.game.example.tank.annotation.TankMessage;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.Position;
import cn.laoshini.game.example.tank.domain.RoomRole;

/**
 * @author fagarine
 */
@Getter
@Setter
@Builder
@ToString
@TankMessage(id = EnterRoomRes.MESSAGE_ID, desc = "进入房间返回消息")
public class EnterRoomRes {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.ENTER_ROOM_REQ + 1;

    private Integer roomId;

    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 玩家自己的角色id
     */
    private Long roleId;

    /**
     * 玩家使用的名字
     */
    private String nick;

    /**
     * 玩家头像或者坦克类型
     */
    private Integer portrait;

    /**
     * 排行
     */
    private Integer rank;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 死亡次数
     */
    private Integer deadNum;

    /**
     * 击杀数
     */
    private Integer killNum;

    /**
     * 玩家朝向，0为默认朝向
     */
    private Integer direction;

    /**
     * 玩家移动速度
     */
    private Integer speed;

    /**
     * 玩家出现的坐标
     */
    private Position pos;

    /**
     * 已在房间内的玩家信息
     */
    private List<RoomRole> roles;
}
