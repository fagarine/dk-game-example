package cn.laoshini.game.example.tank.message.gm.module;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import cn.laoshini.dk.domain.dto.ModuleInfoDTO;
import cn.laoshini.game.example.tank.annotation.TankMessage;
import cn.laoshini.game.example.tank.constant.TankConstants;

/**
 * @author fagarine
 */
@Getter
@Setter
@ToString
@TankMessage(id = GetModuleListRes.MESSAGE_ID, gm = true)
public class GetModuleListRes {

    public static final int MESSAGE_ID = TankConstants.GM_HEAD + TankConstants.GET_MODULE_LIST_REQ + 1;

    private List<ModuleInfoDTO> modules;

}
