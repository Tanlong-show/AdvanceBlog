package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.Article;
import cn.edu.tl.blog.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer>{

    //双方信息全部获取
    @Query(nativeQuery=true, value = "select * from chat u where (u.user_id = ?1 and u.friend_id = ?2)or(u.friend_id = ?1 and u.user_id = ?2)")
    public List<Chat> findAllByUserIdAndFriendId(Integer userId, Integer friendId);

    //双方信息全部获取,这里数据需要反过来，获取对方的！
    @Query(nativeQuery=true, value = "select * from chat u where (u.user_id = ?1 and u.friend_id = ?2 and u.id > ?3)")
    public List<Chat> findNewMessage(Integer userId, Integer friendId, Integer id);

}
