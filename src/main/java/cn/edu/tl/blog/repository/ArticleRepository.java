package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.ArticleUser;
import cn.edu.tl.blog.entity.Article;
import cn.edu.tl.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    public List<Article> findArticlesByUserId(Integer userId);

    public List<Article> findArticlesByType(String type);

    public List<Article> findArticlesByTypeAndUserId(String type, Integer id);

    @Query(nativeQuery = true, value = "select * from article u where u.id = ?1")
    public Article findArticleById(Integer id);

    @Query(value = "select new cn.edu.tl.blog.entity.ArticleUser(a.title,a.content,a.summarize,a.type,a.cover,a.articleDate,u.name,u.headpicture,a.pageLikes,a.pageViews,a.comments,a.collects,a.recommends,a.id,a.userId) from User u,Article a where a.userId = u.id")
    public List<ArticleUser> articleUser();

    //查找推文
    @Query(value = "select new cn.edu.tl.blog.entity.ArticleUser(a.title,a.content,a.summarize,a.type,a.cover,a.articleDate,u.name,u.headpicture,a.pageLikes,a.pageViews,a.comments,a.collects,a.recommends,a.id,a.userId) from User u,Article a where u.id=?1 and a.id = ?2")
    public ArticleUser recommendarticleUser(Integer userId, Integer articleId);

    //通过文章内容/标题，模糊查询，带分页
    public Page<Article> findByContentContainingOrTitleContaining(String content, String title, Pageable pageable);

    //通过文章内容/标题，模糊查询，不带分页
    public List<Article> findByContentContainingOrTitleContaining(String content, String title);

    //通过文章内容/标题，模糊查询，不带分页,带用户
    @Query(nativeQuery = true, value = "select * from article u where u.user_id = ?1 and (u.content like %?2% or u.title like %?3%)")
    public List<Article> findByContentContainingOrTitleContainingAndUserId(int id, String content, String title);

    //不同专业人被推文，收藏
    @Query(value = "select new cn.edu.tl.blog.entity.ArticleUser(a.title,a.content,a.summarize,a.type,a.cover,a.articleDate,u.name,u.headpicture,a.pageLikes,a.pageViews,a.comments,a.collects,a.recommends,a.id,a.userId) from User u,Article a where a.userId = u.id and u.speciality = ?1")
    public List<ArticleUser> selectpk(String spec);

    //通过文章类别查询，带分页
    public Page<Article> findByType(String Type,Pageable pageable);

    public void deleteArticleByUserId(Integer userId);

}