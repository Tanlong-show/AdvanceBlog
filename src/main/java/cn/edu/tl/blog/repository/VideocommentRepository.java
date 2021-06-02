package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.User;
import cn.edu.tl.blog.entity.Videocomment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideocommentRepository extends JpaRepository<Videocomment, Integer> {
    public List<Videocomment> findAllByVideoId(Integer id);
}
