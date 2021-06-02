package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.User;
import cn.edu.tl.blog.entity.Usercollect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface UsercollectRepository extends JpaRepository<Usercollect, Integer> {

    public Usercollect findByUserIdAndArticleId(Integer userId, Integer articleId);

    public List<Usercollect> findAllByUserId(Integer userId);

    //删除
    @Modifying
    @Transactional
    @Query(nativeQuery=true, value = "delete from usercollect  where user_id = ?1 and article_id = ?2")
    public void deleteByUserIdIsAndArticleIdIs(int userId, int articleId);

    @Modifying
    @Transactional
    public void deleteByArticleId(Integer id);

    public void deleteByUserId(Integer id);

    public List<Usercollect> findAllByColtimeContainingAndUserId(String s,Integer i);


}
