package realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MyRealm
 *
 * @author Chan
 * @since 2020/9/3
 */
public class MyRealm implements Realm {

    private final static Logger log = LoggerFactory.getLogger(MyRealm.class);

    public String getName() {
        return "MyRealm";
    }

    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof UsernamePasswordToken;
    }

    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = authenticationToken.getPrincipal().toString();
        String password =new String((char[])authenticationToken.getCredentials());

        if(!"chan".equals(username)) {
            throw new UnknownAccountException(); //如果用户名错误
        }
        if(!"123456".equals(password)) {
            throw new IncorrectCredentialsException(); //如果密码错误
        }
        //如果身份认证验证成功，返回一个AuthenticationInfo实现；
        return new SimpleAuthenticationInfo(username, password, getName());
    }
}
