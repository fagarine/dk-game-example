package cn.laoshini.game.example.tank.manage.ini;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.laoshini.game.example.tank.entity.StaticBullet;
import cn.laoshini.game.example.tank.entity.StaticTank;

/**
 * @author fagarine
 */
@Component
public class StaticDataManager {

    @Resource
    private StaticDataDao staticDataDao;

    private Map<Integer, StaticBullet> bulletMap;

    private Map<Integer, StaticTank> tankMap;

    public void load() {
        initBullet();
        initTank();
    }

    public void initTank() {
        List<StaticTank> tanks = staticDataDao.selectTanks();
        tankMap = new HashMap<>(tanks.size());
        for (StaticTank tank : tanks) {
            tankMap.put(tank.getTankId(), tank);
        }
    }

    public void initBullet() {
        List<StaticBullet> bullets = staticDataDao.selectBullets();
        bulletMap = new HashMap<>(bullets.size());
        for (StaticBullet bullet : bullets) {
            bulletMap.put(bullet.getBulletId(), bullet);
        }
    }

    public Map<Integer, StaticBullet> getBulletMap() {
        return bulletMap;
    }

    public StaticBullet getBulletById(int bulletId) {
        return bulletMap.get(bulletId);
    }

    public Map<Integer, StaticTank> getTankMap() {
        return tankMap;
    }

    public StaticTank getTankById(int tankId) {
        return tankMap.get(tankId);
    }
}
