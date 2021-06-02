package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.Friendship;
import cn.edu.tl.blog.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;

public interface RecommendRepository extends JpaRepository<Recommend, Integer> {
    @Modifying
    @Transactional
    public  void deleteByArticleId(Integer id);

    public void deleteByUserId(Integer id);

}
