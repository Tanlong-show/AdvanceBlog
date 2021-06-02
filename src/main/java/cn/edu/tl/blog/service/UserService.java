package cn.edu.tl.blog.service;

import cn.edu.tl.blog.entity.User;

public interface UserService {
    //查看该用户是否存在
    public User findUser(Integer userId);
    //添加新用户
    public void addUser(User user);

}
