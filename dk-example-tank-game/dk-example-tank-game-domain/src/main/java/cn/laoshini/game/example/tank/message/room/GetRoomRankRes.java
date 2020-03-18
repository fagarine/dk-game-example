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
@TankMessage(id = GetRoomRankRes.MESSAGE_ID)
public class GetRoomRankRes {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.GET_ROOM_RANK_REQ + 1;

    /**
     * 玩家的排行，0为未上榜
     */
    private int myRank;

    /**
     * 排行信息
     */
    private List<RoomRankDTO> ranks;

}
