package cn.laoshini.game.example.tank.message.room;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.laoshini.game.example.tank.annotation.TankMessage;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.dto.RoomRankDTO;

/**
 * @author fagarine
 */
@Getter
@Setter
@Builder
@ToString
@TankMessage(id = RoomRankChangePush.MESSAGE_ID)
public class RoomRankChangePush {
    public static final int MESSAGE_ID = TankConstants.PUSH_HEAD + TankConstants.ROOM_RANK_CHANGE_PUSH;

    /**
     * 排行信息
     */
    private List<RoomRankDTO> ranks;

}
