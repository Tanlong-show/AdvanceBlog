package cn.edu.tl.blog.config;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    //shiroFilterFactoryBean：3
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);
        //添加shiro的内置过滤器
        /*
            anon:无需 认证
            authc:必须 认证 了才能访问

            user:必须拥有记住我功能才能用

            perms:拥有对某个资源的 权限 才能访问
            role:拥有某个角色 权限 才能访问
         */
        Map<String, String> filterMap = new LinkedHashMap<>();
//        filterMap.put("/login", "anon");
        //user指的是用户认证通过或者配置了Remember Me记住用户登录状态后可访问。
//        filterMap.put("/**", "user");
        //也就是root为2，2的权限即为管理员权限
        filterMap.put("/index/usermanage","perms[2]");
        filterMap.put("/user/blogmanage","perms[2]");

        bean.setFilterChainDefinitionMap(filterMap);

        //未认证则进行登录请求，也就是userRealm的doGetAuthenticationInfo
        bean.setLoginUrl("/login");
        //未授权页面跳转,红色警告，doGetAuthorizationInfo
        bean.setUnauthorizedUrl("/login/noPower");

        return bean;
    }
    //DefaultWebSecurityManager：2
    @Bean(name="securityManager")
    public DefaultWebSecurityManager getdefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm,
                                                                  @Qualifier("rememberMeManager") RememberMeManager rememberMeManager){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        securityManager.setRememberMeManager(rememberMeManager);
        return securityManager;
    }

    //创建realm对象：1
    @Bean
    public UserRealm userRealm(){
        return new UserRealm();
    }



    //记住我
    @Bean(name = "rememberMeManager")
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //这个地方有点坑，不是所有的base64编码都可以用，长度过大过小都不行，没搞明白，官网给出的要么0x开头十六进制，要么base64
        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }
    //cookie管理
    @Bean
    public SimpleCookie rememberMeCookie() {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setMaxAge(1 * 60 * 60);
        return cookie;
    }


}


