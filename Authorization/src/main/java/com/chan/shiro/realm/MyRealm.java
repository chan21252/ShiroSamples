package com.chan.shiro.realm;

import com.chan.shiro.common.MD5Util;
import com.chan.shiro.model.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * MyRealm
 *
 * @author Chan
 * @since 2021/1/12
 */
public class MyRealm extends AuthorizingRealm {

    private final static Logger logger = LoggerFactory.getLogger(MyRealm.class);

    /**
     * 授权
     *
     * @param principals PrincipalCollection
     * @return AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username= principals.getPrimaryPrincipal().toString();

        Set<String> roleNameSet = new HashSet<String>();
        roleNameSet.add("系统管理员");
        roleNameSet.add("运维工程师");

        Set<String> permissionNameSet = new HashSet<String>();
        permissionNameSet.add("sys:user:create");
        permissionNameSet.add("sys:user:update");
        permissionNameSet.add("sys:user:delete");
        permissionNameSet.add("sys:user:list");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roleNameSet);
        info.addStringPermissions(permissionNameSet);

        logger.info("授权完成");

        return info;
    }

    /**
     * 认证
     * @param token Token
     * @return AuthenticationInfo
     * @throws AuthenticationException AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();
        String password = new String(usernamePasswordToken.getPassword());

        //模拟数据库
        Map<String, User> userMap = new HashMap<String, User>();
        //明文密码123
        User user1 = new User("admin", "044a541ea11ad54564acbc1ef616e213", "1d175a7ec5f24d16a1f70ab132be05e8",0);
        userMap.put("admin", user1);

        //用户名正确
        if (!userMap.containsKey(username)) {
            throw new UnknownAccountException("用户名不存在");
        }

        //检查密码
        User currentUser = userMap.get(username);
        String passwordSalt = MD5Util.md5privateSalt(username + password, currentUser.getSalt());
        logger.info("用户名：" + username);
        logger.info("密码：" + password);
        logger.info("私盐：" + currentUser.getSalt());
        logger.info("待验证的加密密码：" + passwordSalt);
        logger.info("正确的加密密码：" + currentUser.getPassword());

        if (currentUser.getStatus() != 0) {
            throw new DisabledAccountException("用户禁止登录");
        }
        if (currentUser.getPassword().equals(passwordSalt)) {
            logger.info("认证成功");
            return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
        } else {
            throw new CredentialsException("密码错误");
        }
    }
}