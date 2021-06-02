package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByUserIdAndPassword(Integer userId, String password);

    //通过学号查人
    public User findByUserId(Integer userId);
    //通过姓名，模糊查询
    @Query(value = "select user from User user where user.name like %?1%")
    public List<User> findUsersByNameLike(String name);

    public User findUserById(Integer id);

    //查询总人数
    @Query(value = "select count(id) from User")
    public int countAll();

    //查询在线人数
    @Query(value = "select count(user.id) from User user where user.state=1")
    public int countgreen();

    public List<User> findByNameContainingOrUserId(String name,Integer userId);

}
