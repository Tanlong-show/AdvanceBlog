package cn.edu.tl.blog.config;

import cn.edu.tl.blog.entity.User;
import cn.edu.tl.blog.repository.UserRepository;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


//自定义的UserRealm
public class UserRealm extends AuthorizingRealm {
    @Autowired
    UserRepository userRepository;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了=》授权");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的user
        User user = (User)subject.getPrincipal();
        //得到数据库中user对应的root权限，2-管理员
        info.addStringPermission(""+user.getRoot());
        return info;
    }
    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行了=》认证");
        UsernamePasswordToken userToken = (UsernamePasswordToken)authenticationToken;
        //这里name实际上是id学号
        User user;
        if(userToken.getUsername().equals("null")){
            return null;
        }
        user = userRepository.findByUserId(Integer.parseInt(userToken.getUsername()));
        if(user == null){
            return null;//用户不存在
        }

        //这里的密码被加密了，成了charat。
        //第一个参数则是把user存到subject里
        return new SimpleAuthenticationInfo(user,user.getPassword(),"");
    }

}
