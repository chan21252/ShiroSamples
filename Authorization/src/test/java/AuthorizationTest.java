import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AuthorizationTest
 *
 * @author Chan
 * @since 2021/1/11
 */
public class AuthorizationTest {

    private final static Logger logger = LoggerFactory.getLogger(AuthorizationTest.class);

    @Test
    public void testCheckRole() {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("admin", "123");

        subject.login(token);
        if (subject.isAuthenticated()) {
            logger.info("检查用户是否有系统管理员角色：" + subject.hasRole("系统管理员"));
            logger.info("检查用户是否有新增权限：" + subject.isPermitted("sys:user:create"));
        }
        subject.logout();
    }
}
