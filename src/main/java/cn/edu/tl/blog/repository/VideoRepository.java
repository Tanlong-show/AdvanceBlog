package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.User;
import cn.edu.tl.blog.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Integer> {

    public List<Video> findAll();

    public Video findVideoById(Integer id);

    //关键词模糊查询
    public List<Video> findByTitleContainingOrUserNameContainingOrSummarizeContainingOrTypeContaining(String keyword,String keyword1,String keyword2,String keywords);

}
