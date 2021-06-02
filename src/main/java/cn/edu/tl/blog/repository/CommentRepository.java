package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.Comment;
import cn.edu.tl.blog.entity.CommentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Integer> {
        public List<Comment> findAllByArticleId(Integer articleId);
        @Query(value = "select new cn.edu.tl.blog.entity.CommentUser(c.content,c.commentDate,u.name,u.headpicture,u.id,c.id,c.articleId) from Comment c,User u where c.articleId=?1 and u.id = c.userId")
        public List<CommentUser> findComment(Integer articleId);

        @Modifying
        @Transactional
        //通过文章id删除评论
        public void deleteByArticleId(Integer id);

        public void deleteByUserId(Integer id);

}
