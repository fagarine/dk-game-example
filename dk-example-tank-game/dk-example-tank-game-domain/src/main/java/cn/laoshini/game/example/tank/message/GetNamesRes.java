package cn.laoshini.game.example.tank.message;

import java.util.List;

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
@TankMessage(id = GetNamesRes.MESSAGE_ID)
public class GetNamesRes {

    public static final int MESSAGE_ID = TankConstants.MESSAGE_HEAD + TankConstants.GET_NAMES_REQ + 1;

    /**
     * 随机名字
     */
    private List<String> names;
}
