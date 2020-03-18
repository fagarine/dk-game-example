package cn.laoshini.game.example.tank.manage.player;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import cn.laoshini.dk.dao.IDefaultDao;
import cn.laoshini.dk.dao.query.QueryUtil;
import cn.laoshini.game.example.tank.entity.Account;
import cn.laoshini.game.example.tank.entity.Role;

/**
 * @author fagarine
 */
@Repository
public class AccountDao {

    @Resource
    private IDefaultDao defaultDao;

    public Account selectById(int accountId) {
        return defaultDao.selectEntity(Account.class, QueryUtil.newBeanQueryCondition("accountId", accountId));
    }

    public Account selectByDeviceNo(String deviceNo) {
        return defaultDao.selectEntity(Account.class, QueryUtil.newBeanQueryCondition("deviceNo", deviceNo));
    }

    public List<Account> selectAllAccount() {
        return defaultDao.selectAllEntity(Account.class);
    }

    public void insertAccount(Account account) {
        defaultDao.saveEntity(account);
    }

    public void updateAccount(Account account) {
        defaultDao.updateEntity(account);
    }

    public List<Role> selectAllRole() {
        return defaultDao.selectAllEntity(Role.class);
    }

    public Role selectRoleById(long roleId) {
        return defaultDao.selectEntity(Role.class, QueryUtil.newBeanQueryCondition("roleId", roleId));
    }

    public void insertRole(Role role) {
        defaultDao.saveEntity(role);
    }

    public void updateRole(Role role) {
        defaultDao.updateEntity(role);
    }
}
