package cn.edu.tl.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Article {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private Integer pageViews;
    private Integer pageLikes;
    private Integer comments;
    private Integer collects;
    private Integer recommends;

    public void setArticleDate(String articleDate) {
        this.articleDate = articleDate;
    }

    public void setSummarize(String summarize) {
        this.summarize = summarize;
    }



    private String articleDate;
    private String summarize;
    private String cover;
    private String type;

    public Integer getRecommends() {
        return recommends;
    }

    public void setRecommends(Integer recommends) {
        this.recommends = recommends;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public String getArticleDate() {
        return articleDate;
    }

    public String getSummarize() {
        return summarize;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPageViews() {
        return pageViews;
    }

    public void setPageViews(Integer pageViews) {
        this.pageViews = pageViews;
    }

    public Integer getPageLikes() {
        return pageLikes;
    }

    public void setPageLikes(Integer pageLikes) {
        this.pageLikes = pageLikes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCollects() {
        return collects;
    }

    public void setCollects(Integer collects) {
        this.collects = collects;
    }
}
