package cn.edu.tl.blog.service;

import cn.edu.tl.blog.entity.Article;
import cn.edu.tl.blog.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@Service
public class ArticleServiceimpl {

    @Autowired
    private ArticleRepository articleRepository;

    public Page<Article> getArticleWithPageByKeyWord(Integer page, Integer size, String keyword) {
        if (page <= 0) {
            page = 1;
        }
        //排序
        Sort sort = Sort.by(Sort.Direction.DESC, "pageLikes");
        //分页
        Pageable pageRequest = PageRequest.of(page - 1, size, sort);
        Page<Article> articleList = articleRepository.findByContentContainingOrTitleContaining(keyword,keyword,pageRequest);

        return articleList;
    }

    public Page<Article> getArticleByType(Integer page, Integer size, String type) {
        //排序
        Sort sort = Sort.by(Sort.Direction.DESC, "pageLikes");
        //分页
        Pageable pageRequest = PageRequest.of(page - 1, size, sort);
        Page<Article> articleList = articleRepository.findByType(type,pageRequest);
        return articleList;

    }


}
