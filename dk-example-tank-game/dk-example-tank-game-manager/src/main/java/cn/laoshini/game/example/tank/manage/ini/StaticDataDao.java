package cn.laoshini.game.example.tank.manage.ini;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import cn.laoshini.dk.dao.IDefaultDao;
import cn.laoshini.game.example.tank.entity.StaticBullet;
import cn.laoshini.game.example.tank.entity.StaticTank;

/**
 * @author fagarine
 */
@Repository
public class StaticDataDao {

    @Resource
    private IDefaultDao defaultDao;

    public List<StaticBullet> selectBullets() {
        return defaultDao.selectAllEntity(StaticBullet.class);
    }

    public List<StaticTank> selectTanks() {
        return defaultDao.selectAllEntity(StaticTank.class);
    }
}
