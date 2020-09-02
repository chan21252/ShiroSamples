原文地址：http://shiro.apache.org/reference.html

# 介绍

## 什么是Apache Shiro？

Apache Shiro是一个强大而灵活的开源安全框架，可以简明地处理身份验证、授权、企业会话管理和加密。

Apache Shiro首要目标是易于使用和理解。安全有时候会很复杂，甚至令人痛苦，但没必要。一个框架应该尽可能隐藏复杂性，公开简洁直观的API，简化开发者应用安全的方面的工作。

可以使用Apache Shiro做的一些事情：

- 验证用户身份
- 用户访问控制，例如：
  - 确定用户是否被分配了某个安全角色
  - 确定用户是否被允许做某件事
- 在任何环境中使用会话API，甚至无需web或者EJB容器
- 反应认证、访问控制或者会话的生命周期期间的事件
- 聚合1个或多个用户安全数据数据源，并将其全部呈现为组合用户视图
- 支持单点登录（sso）功能
- 支持“记住我”，关联用户无需登录

Shiro试图在所有应用程序环境中实现这些目标——从最简单的命令行应用程序到最大的企业应用程序，而不强制依赖其他第三方框架、容器或应用服务器。当然，项目的目标是尽可能地集成到这些环境中，但是它可以在任何环境中开箱即用。

## Apache Shiro特性

![shiro-features](http://shiro.apache.org/assets/images/ShiroFeatures.png)

四大基石：

- 身份验证：就是登录
- 授权：访问控制，确定谁可以访问什么。
- 会话管理：管理特定于用户的会话，即使是非web或EJB应用程序
- 加密：使用加密算法保持数据安全

其他支持：

- web支持
- 缓存
- 并发
- 测试
- 以其他身份运行
- 记住我

# 快速入门

## 创建Maven项目，引入Shiro

pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.chan.shiro</groupId>
    <artifactId>QuickStart</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-core</artifactId>
            <version>1.6.0</version>
        </dependency>
        <!-- 使用sl4j记录日志 -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.30</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>jcl-over-slf4j</artifactId>
            <version>1.7.30</version>
        </dependency>
    </dependencies>
</project>
```

## Shiro组件

### SecurityManager

SecurityManager是Shiro环境的核心组件，每个应用程序都必须有的。下面我们会编写一个ini配置文件，稍后用来初始化SecurityManager。

### Subject

Subject代表特定安全视图下的当前用户，可以是人，也可以是第三方应用、定时任务等。大多数情况，可以理解为Shiro的用户。

## 使用Shiro

### shiro.ini

```ini
# ----------------------------------------------------------
# Users and their (optional) assigned roles
# username = password, role1, role2, ..., roleN
# ----------------------------------------------------------
[users]
root = 123456, admin
chan = 123456, employee
cc = 123456, boss

# ----------------------------------------------------------
# Roles with assigned permissions
# roleName = perm1, perm2, ..., permN
# ----------------------------------------------------------
[roles]
admin = *
employee = employee:*
boss = boss:*, employee:*
```

### 简单登录

1. 创建SecurityManager
   1. 使用ini文件的配置，创建SecurityManager工厂
   2. 工厂获取SecurityManager实例
   3. SecurityUtils注入SecurityManager
2. 获取Subject
   1. SecurityUtils获取Subject
   2. 此时Subject为匿名
3. 身份验证
   1. 创建token
   2. login
   3. 异常处理

```java
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
```

输出

```
[main] INFO Tutorial - 我的第一个shiro应用
[main] INFO org.apache.shiro.session.mgt.AbstractValidatingSessionManager - Enabling session validation scheduler...
[main] INFO Tutorial - 用户 [root] 登录成功！
[main] INFO Tutorial - 您是管理员
[main] INFO Tutorial - 您有查看 employee:info 的权限
[main] INFO Tutorial - 退出登录
```

# 架构

## 顶层设计

![](http://shiro.apache.org/assets/images/ShiroBasicArchitecture.png)

### Subject

Subject本质上是当前执行用户特定安全性的“视图”。 “用户”一词通常表示一个人，而Subject可以是一个人，但是它也可以表示第三方服务，守护程序，定时任务-基本上是当前与该软件交互的任何东西。

Subject都绑定到SecurityManager。 与Subject进行交互时，这些交互会转换为SecurityManager的特定Subject交互。

### SecurityManager

SecurityManager是Shiro体系的核心，充当一种伞形对象，协调其内部安全组件共同构成一个对象图。然而，一旦为应用程序配置好了SecurityManager，通常就不用管它了，开发人员几乎把所有时间都花在Subject API上。

当你与一个Subject交互时，实际上是SecurityManager为所有Subject安全操作在后台干活。

### Realm

Realm充当Shiro和应用程序安全数据之间的“桥梁”。当执行身份验证和授权等数据安全操作时，Shiro会从应用程序配置的一个或多个Realm中查找内容。

Realm本质上是一个安全相关数据的DAO:它封装了数据源的连接细节，并使Shiro在需要时可以使用相关数据。SecurityManager可以配置多个Realm，但至少有一个。

与其他内部组件一样，Shiro SecurityManager管理Realm获取安全和身份数据，然后被表示为Subject实例。

## 具体架构

![](http://shiro.apache.org/assets/images/ShiroArchitecture.png)

### Authenticator（验证器）

执行用户身份验证（登录）的组件。当用户登录的时候，Authenticator执行相关逻辑，协调用户账户相关的Realm。

Authentication Strategy （验证器策略）：如果配置了多个Realm，验证器策略会协调Realm，决定身份验证成功或失败的条件。比如一个Realm成功了，其他失败了，验证算成功吗？

### Authorizer（授权者）

Authorizer是应用中负责用户访问控制的组件。它最终决定用户可以做什么。Authorizer还知道如何与多个后端数据源协调，来访问角色和权限信息，使用这些信息来确定用户是否能执行相关操作。

### SessionManager （会话管理）

SessionManager用来创建和管理会话生命周期，为所有环境中的用户提供健壮的会话体验。Shiro会使用应用环境已经有的会话机制（比如servlet容器），如果没有，则会使用自带的企业级会话管理。

SessionDAO ：执行session持久性的CURD操作。

### CacheManager （缓存管理）

CacheManager创建和管理其他Shiro组件使用的Cache实例生命周期。 因为Shiro可以访问众多后端数据源以进行身份验证、授权和会话管理，为了提高性能，缓存一直是框架中一流特性。 可以将任何现代的开源或企业缓存产品插入Shiro，以提供快速有效的用户体验。

### Cryptography （加密）

Shiro的`crypto`包包含了加密算法、哈希和不同编码解码器的实现。

### 总结

1. 开发人员使用Subject API进行身份验证、授权等操作。
2. SecurityManager管理Subject实例，协调内部各个组件完成逻辑。
3. Realm从外部读取数据安全配置，供SecurityManager的组件使用。

# 配置

## 编码配置

## INI文件配置

ini是一种由键值对组成的基础文本配置。可以由有多个名称不重复的小节组成，每个小节中的key不允许重复。#用来表示行注释。

Shiro的ini配置文件示例：

```ini
# =======================
# Shiro INI configuration
# =======================

[main]
# Objects and their properties are defined here,
# Such as the securityManager, Realms and anything
# else needed to build the SecurityManager

[users]
# The 'users' section is for simple deployments
# when you only need a small number of statically-defined
# set of User accounts.

[roles]
# The 'roles' section is for simple deployments
# when you only need a small number of statically-defined
# roles.

[urls]
# The 'urls' section is used for url-based security
# in web applications.  We'll discuss this section in the
# Web documentation
```

### [mian]

配置SecurityManager 和它依赖的对象实例，比如Realm。

```ini
[main]
sha256Matcher = org.apache.shiro.authc.credential.Sha256CredentialsMatcher

# 实例化了一个自定义Realm对象，后续可以用名称myRealm引用该对象
myRealm = com.company.shiro.realm.MyRealm
# 设置对象属性
myRealm.connectionTimeout = 30000
myRealm.setUsername("jsmith");
myRealm.password = secret
# ${变量名}引用
myRealm.credentialsMatcher = $sha256Matcher

# 属性嵌套
securityManager.sessionManager.globalSessionTimeout = 1800000

# 字节数组，原始字节数组无法以文本格式原生指定，必须使用字节数组的文本编码，比如base64编码，16进制编码
securityManager.rememberMeManager.cipherKey = kPH+bIxk5D2deZiIxcaaaA==
securityManager.rememberMeManager.cipherKey = 0x3707344A4093822299F31D008

# List，Set属性
sessionListener1 = com.company.my.SessionListenerImplementation
sessionListener2 = com.company.my.other.SessionListenerImplementation
securityManager.sessionManager.sessionListeners = $sessionListener1, $sessionListener2

# Map属性
object1 = com.company.some.Class
object2 = com.company.another.Class
anObject = some.class.with.a.Map.property
anObject.mapProperty = key1:$object1, key2:$object2

# 指定自己的securityManager实现。一般无需实例化，有默认实现
securityManager = com.company.security.shiro.MyCustomSecurityManager
```

> 对象实例化和设置属性的顺序和[main]中配置的顺序一致，后面的相同配置会覆盖前面的配置

### [users]

```ini
[users]
# 定义静态用户数据
# username = password, roleName1, roleName2, …, roleNameN
admin = secret
lonestarr = vespa, goodguy, schwartz
darkhelmet = ludicrousspeed, badguy, schwartz
```

**自动IniRealm**

[users]或者[roles]节是空的会自动触发创建`org.apache.shiro.realm.text.IniRealm`实例，并且可以在[main]中直接配置它的属性。

**密码加密**

一旦指定了经过散列处理的文本密码值，就必须告诉Shiro这些值是加密的。配置[main]部分中隐式创建的iniRealm，注入与散列算法相匹配的credentialsMatcher实现

```ini
[main]
sha256Matcher = org.apache.shiro.authc.credential.Sha256CredentialsMatcher
iniRealm.credentialsMatcher = $sha256Matcher

[users]
# user1 = sha256-hashed-hex-encoded password, role1, role2, ...
user1 = 2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b, role1, role2, ...
```

密码加盐，参考[org.apache.shiro.authc.credential.HashedCredentialsMatcher](http://shiro.apache.org/static/1.6.0/apidocs/org/apache/shiro/authc/credential/HashedCredentialsMatcher.html)的文档。

### [roles]

将权限与[users]中定义的角色相关联。有不需要权限关联的角色，就不要在[roles]部分中列出它们。如果角色还不存在，那么仅在[users]部分定义角色名称就代表创建该角色。

```ini
[roles]
# rolename = permissionDefinition1, permissionDefinition2, …, permissionDefinitionN
# 'admin' role has all permissions, indicated by the wildcard '*'
admin = *
# The 'schwartz' role can do anything (*) with any lightsaber:
schwartz = lightsaber:*
# The 'goodguy' role is allowed to 'drive' (action) the winnebago (type) with
# license plate 'eagle5' (instance specific id)
goodguy = winnebago:drive:eagle5
```

权限相关参考《[Understanding Permissions in Apache Shiro](http://shiro.apache.org/permissions.html)》。

### [urls]

web相关。//to-do

# 核心

## 身份验证

### 验证Subject

步骤：

1. 创建令牌，令牌一般由Subject标识（principal）和凭据（credentials）组成
2. 提交令牌，进行身份验证
3. 如果验证成功，允许访问，否则拒绝。

### Remembered 和 Authenticated的区别

Remembered ：subject.isRemembered()返回true，说明Suject有一个由之前会话记住的身份。

Authenticated：subject.isAuthenticated()返回true，说明Subject已经被当前会话成功验证了身份。

Authenticated可以强有力地证明Subject身份的，Remembered并不能。建议常规操作Remembered即可，而敏感操作需要Authenticated重新验证身份。

### 登出

subject.logout()进行登出操作。登出会销毁一切身份标识，Subject会变成匿名的。

### 原理

![](http://shiro.apache.org/assets/images/ShiroAuthenticationSequence.png)

1. 应用调用Subject.login(token)开始验证
2. Subject委托验证工作给SecurityManager，通过调用SecurityManager.login(token)
3. SecurityManager收到token后，委托给内部的Authenticator验证器
4. 验证器协调Realm，利用AuthenticationStrategy（验证策略）进行身份验证。验证策略会对Realm验证结果作出决策。
5. Realm验证提交的token。

**Authenticator**

Shiro默认的Authenticator是`ModularRealmAuthenticator`

自定义Authenticator：

```ini
[main]
authenticator = com.foo.bar.CustomAuthenticator
securityManager.authenticator = $authenticator
```

**AuthenticationStrategy**

验证器策略决策多个Realm的最终验证结果，也负责聚合每个验证成功Realm的结果，并将它们绑定到AuthenticationInfo。Authenticator实例会返回AuthenticationInfo，Shiro使用它来表示Subject的最终身份。

验证策略会有4处进行决策

1. 所有Realm调用之前
2. 每个Realm的getAuthenticationInfo方法之前
3. 每个Realm的getAuthenticationInfo方法之后
4. 所有Realm调用之后

Shiro有3个AuthenticationStrategy实现：

| 策略                         | 说明                                                         |
| ---------------------------- | ------------------------------------------------------------ |
| AtLeastOneSuccessfulStrategy | 只要有一个Realm验证成功，则成功。没有成功的，则失败。ModularRealmAuthenticator的默认策略。 |
| FirstSuccessfulStrategy      | 只使用第一个成功的Realm验证信息，后面都将忽略。如果没有成功的，则失败。 |
| AllSuccessfulStrategy        | 所有的Realm都必须验证成功，才成功。                          |

指定验证器策略

```ini
[main]
authcStrategy = org.apache.shiro.authc.pam.FirstSuccessfulStrategy
securityManager.authenticator.authenticationStrategy = $authcStrategy
```

自定义策略：实现`org.apache.shiro.authc.pam.AbstractAuthenticationStrategy`类。

**Realm验证顺序**

```ini
blahRealm = com.company.blah.Realm
fooRealm = com.company.foo.Realm
barRealm = com.company.another.Realm
securityManager.realms = $blahRealm, $fooRealm, $barRealm
```

1. 设置了securityManager.realms属性
   1. 按照属性顺序验证（迭代顺序）
   2. 不会验证没有注入的Realm
2. 没有设置securityManager.realms
   1. 加载所有定义的Realm（隐式设置）
   2. 按照配置文件Realm定义的顺序验证



## 授权

### 授权的三要素

**权限**

安全策略的原子要素，描述了谁是否可以对资源进行某种操作。

**角色**

一组权限的集合。

**用户**

应用程序的使用者，Shiro中的用户是Subject。角色拥有某些权限，将角色分配给用户，用户就具有了执行某些操作的权限。

常见关联关系：用户1-n角色1-n权限

### Subject授权

三种方式：

1. 编码方式
2. 注解方式
3. JSP TagLibs 控制web输出







