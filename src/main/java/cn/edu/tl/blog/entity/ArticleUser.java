package cn.edu.tl.blog.entity;

import java.io.Serializable;

public class ArticleUser implements Serializable {
    private String title;
    private String content;
    private String summarize;
    private String type;
    private String cover;
    private String ariticleDate;
    private String userName;
    private Integer userId;
    private String headpicture;
    private Integer pageLikes;
    private Integer pageViews;
    private Integer comments;
    private Integer collects;
    private Integer recommends;
    private Integer id;//文章id

    public ArticleUser(String title, String content, String summarize, String type, String cover, String ariticleDate, String userName, String headpicture, Integer pageLikes, Integer pageViews, Integer comments, Integer collects, Integer recommends,Integer id, Integer userId) {
        this.title = title;
        this.content = content;
        this.summarize = summarize;
        this.type = type;
        this.cover = cover;
        this.ariticleDate = ariticleDate;
        this.userName = userName;
        this.headpicture = headpicture;
        this.pageLikes = pageLikes;
        this.pageViews = pageViews;
        this.comments = comments;
        this.collects = collects;
        this.recommends = recommends;
        this.id = id;
        this.userId = userId;
    }
    public ArticleUser(){

    }
    //拷贝构造
    public ArticleUser(ArticleUser articleUser){
        this.setTitle(articleUser.getTitle());
        this.setContent(articleUser.getContent());
        this.setSummarize(articleUser.getSummarize());
        this.setType(articleUser.getType());
        this.setCover(articleUser.getCover());
        this.setAriticleDate(articleUser.getAriticleDate());
        this.setUserName(articleUser.getUserName());
        this.setHeadpicture(articleUser.getHeadpicture());
        this.setPageLikes(articleUser.getPageLikes());
        this.setPageViews(articleUser.getPageViews());
        this.setComments(articleUser.getComments());
        this.setCollects(articleUser.getCollects());
        this.setRecommends(articleUser.getRecommends());
        this.setId(articleUser.getId());
        this.setUserId(articleUser.getUserId());
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRecommends() {
        return recommends;
    }

    public void setRecommends(Integer recommends) {
        this.recommends = recommends;
    }

    public Integer getPageLikes() {
        return pageLikes;
    }

    public void setPageLikes(Integer pageLikes) {
        this.pageLikes = pageLikes;
    }

    public Integer getPageViews() {
        return pageViews;
    }

    public void setPageViews(Integer pageViews) {
        this.pageViews = pageViews;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Integer getCollects() {
        return collects;
    }

    public void setCollects(Integer collects) {
        this.collects = collects;
    }

    public String getTitle() {
        return title;
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

    public String getSummarize() {
        return summarize;
    }

    public void setSummarize(String summarize) {
        this.summarize = summarize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAriticleDate() {
        return ariticleDate;
    }

    public void setAriticleDate(String ariticleDate) {
        this.ariticleDate = ariticleDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadpicture() {
        return headpicture;
    }

    public void setHeadpicture(String headpicture) {
        this.headpicture = headpicture;
    }
}
