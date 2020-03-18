package cn.laoshini.game.example.tank.message.room;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.laoshini.game.example.tank.annotation.TankMessage;
import cn.laoshini.game.example.tank.constant.TankConstants;
import cn.laoshini.game.example.tank.domain.RoomInfo;

/**
 * @author fagarine
 */
@Getter
@Setter
@Builder
@ToString
@TankMessage(id = GetRoomsRes.MESSAGE_ID)
public class GetRoomsRes {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.GET_ROOMS_REQ + 1;

    private List<RoomInfo> rooms;
}
