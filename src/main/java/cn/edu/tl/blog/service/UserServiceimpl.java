package cn.edu.tl.blog.service;

import cn.edu.tl.blog.entity.User;
import cn.edu.tl.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceimpl implements UserService{
    @Autowired
    UserRepository userRepository;
    //查看该用户是否存在
    public User findUser(Integer userId){
       return userRepository.findByUserId(userId);
    }
    //添加新用户
    public void addUser(User user){
        userRepository.save(user);
    }
}
