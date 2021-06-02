package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.Answer;
import cn.edu.tl.blog.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    public List<Answer> findByArticleId(int articleId);


    @Modifying
    @Transactional
    @Query(value = "delete from answer where comment_id= ?1", nativeQuery = true)
    //通过评论id删除评论
    public void deleteByCommentId(Integer id);
    public void deleteByUserId(Integer id);


    @Modifying
    @Transactional
    @Query(value = "delete from answer where article_id= ?1", nativeQuery = true)
    public void deleteByArticleId(Integer id);


}
