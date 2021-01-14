import com.chan.shiro.common.MD5Util;
import com.chan.shiro.common.ShiroUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * EncryptTest
 *
 * @author Chan
 * @since 2021/1/12
 */
public class EncryptTest {

    private final static Logger logger = LoggerFactory.getLogger(EncryptTest.class);

    @Test
    public void testHashService() {
        DefaultHashService hashService = new DefaultHashService();

        hashService.setHashAlgorithmName("SHA-512");
        hashService.setPrivateSalt(new SimpleByteSource("123"));
        hashService.setGeneratePublicSalt(true);
        hashService.setRandomNumberGenerator(new SecureRandomNumberGenerator());
        hashService.setHashIterations(1);

        HashRequest hashRequest = new HashRequest.Builder()
                .setAlgorithmName("MD5").setSource(ByteSource.Util.bytes("hello"))
                .setSalt(ByteSource.Util.bytes("123")).setIterations(2).build();

        String hex = hashService.computeHash(hashRequest).toHex();

        logger.info(hex);
    }

    @Test
    public void testMD5() {
        Md5Hash md5Hash = new Md5Hash("123", "4", 2);
        logger.info(md5Hash.toString());
        logger.info(md5Hash.toHex());
    }

    @Test
    public void testLoginEncrypt() {
        String username = "admin";
        String password = "123";
        ShiroUtil.login(username, password);
        Subject subject = SecurityUtils.getSubject();
        logger.info("检查用户是否有系统管理员角色：" + subject.hasRole("系统管理员"));
        logger.info("检查用户是否有新增权限：" + subject.isPermitted("sys:user:create"));
    }

    @Test
    public void testUUID() {
        logger.info("UUID：" + UUID.randomUUID().toString().replaceAll("-", "").toLowerCase());
    }

    @Test
    public void testGenerateMD5Password() {
        String username = "admin";
        String password = "123";
        String salt = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();

        String passwordSalt = MD5Util.md5privateSalt(username + password, salt);

        logger.info("私盐：" + salt);
        logger.info("加公盐后又加私盐后的加密密码：" + passwordSalt);
    }
}