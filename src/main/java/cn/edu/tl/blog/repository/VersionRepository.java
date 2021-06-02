package cn.edu.tl.blog.repository;

import cn.edu.tl.blog.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VersionRepository extends JpaRepository<Version, Integer> {

    @Query(value = "select version from Version version")
    public List<Version> findAll();
}
