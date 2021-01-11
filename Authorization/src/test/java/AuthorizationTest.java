import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;

/**
 * AuthorizationTest
 *
 * @author Chan
 * @since 2021/1/11
 */
public class AuthorizationTest {
    @Test
    public void testCheckRole() {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("admin", "123");

        try {
            subject.login(token);
            System.out.println(subject.hasRole("admin"));
        } catch (IncorrectCredentialsException ice) {
            System.out.println(ice.getMessage());
        }

    }
}
