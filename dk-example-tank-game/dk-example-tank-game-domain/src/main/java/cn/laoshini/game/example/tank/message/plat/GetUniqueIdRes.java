package cn.laoshini.game.example.tank.message.plat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.laoshini.game.example.tank.annotation.TankMessage;
import cn.laoshini.game.example.tank.constant.TankConstants;

/**
 * @author fagarine
 */
@Getter
@Setter
@Builder
@ToString
@TankMessage(id = GetUniqueIdRes.MESSAGE_ID)
public class GetUniqueIdRes {

    public static final int MESSAGE_ID = TankConstants.PLAT_MESSAGE_HEAD + TankConstants.GET_UNIQUE_ID_REQ + 1;

    private String uid;

}
