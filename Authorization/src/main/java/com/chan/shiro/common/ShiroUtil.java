package com.chan.shiro.common;

import com.chan.shiro.realm.MyRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ShiroUtil
 *
 * @author Chan
 * @since 2021/1/12
 */
public class ShiroUtil {

    private final static Logger logger = LoggerFactory.getLogger(ShiroUtil.class);

    static {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        //Realm realm1 = new IniRealm("classpath:shiro.ini");
        //securityManager.setRealm(realm1);
        Realm realm2 = new MyRealm();
        securityManager.setRealm(realm2);

        //启用缓存
        CacheManager cacheManager = new MemoryConstrainedCacheManager();
        securityManager.setCacheManager(cacheManager);

        SecurityUtils.setSecurityManager(securityManager);
    }

    public static void login(String username, String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        subject.login(token);
    }
}
