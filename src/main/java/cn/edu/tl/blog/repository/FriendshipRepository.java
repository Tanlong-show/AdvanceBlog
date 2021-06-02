package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.Friendship;
import cn.edu.tl.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {

    public Friendship findByUserIdAndAndFriendId(Integer userId, Integer FriendId);

    public List<Friendship> findAllByUserId(Integer userId);
    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "delete from friendship  where user_id = ?1 and friend_id = ?2")
    public void deleteByUserIdIsAndFriendIdIs(int userId, int FriendId);
    @Modifying
    @Transactional
    public void deleteByUserId(Integer id);
    @Modifying
    @Transactional
    public void deleteByFriendId(Integer id);

}
