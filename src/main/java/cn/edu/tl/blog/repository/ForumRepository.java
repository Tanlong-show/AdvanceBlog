package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.Forum;
import cn.edu.tl.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Integer> {
}
