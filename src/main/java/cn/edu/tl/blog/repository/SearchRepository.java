package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//搜索记录
public interface SearchRepository extends JpaRepository<Search, Integer> {

    public List<Search> findByUserId(Integer userid);
}
