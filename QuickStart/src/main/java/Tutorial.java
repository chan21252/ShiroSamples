import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tutorial
 *
 * @author Chan
 * @since 2020/9/1
 */
public class Tutorial {

    private static final transient Logger log = LoggerFactory.getLogger(Tutorial.class);

    public static void main(String[] args) {
        log.info("我的第一个shiro应用");

        //创建SecurityManager
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        //获取匿名Subject
        Subject currentUser = SecurityUtils.getSubject();

        //身份验证
        if (!currentUser.isAuthenticated()) {
            try {
                UsernamePasswordToken token = new UsernamePasswordToken("root", "123456");
                token.setRememberMe(true);
                currentUser.login(token);
                log.info( "用户 [" + currentUser.getPrincipal() + "] 登录成功！" );

            } catch ( UnknownAccountException uae ) {
                log.error("未知账户：" + uae.getMessage());
            } catch ( IncorrectCredentialsException ice ) {
                log.error("用户名密码不匹配：" + ice.getMessage());
            } catch ( LockedAccountException lae ) {
                log.error("账户已锁定：" + lae.getMessage());
            } catch ( AuthenticationException ae ) {
                log.error(ae.getMessage());
            }
        }

        //权限判断
        if ( currentUser.hasRole( "admin" ) ) {
            log.info("您是管理员" );
        } else {
            log.info( "您不是管理员" );
        }

        if ( currentUser.isPermitted( "employee:info" ) ) {
            log.info("您有查看 employee:info 的权限");
        } else {
            log.info("对不起，您没有有查看 employee:info 的权限");
        }

        //登出
        currentUser.logout();
        log.info("退出登录");

        System.exit(0);
    }
}
