package cn.edu.tl.blog.entity;

import java.io.Serializable;
import java.util.Date;

public class CommentUser implements Serializable {
    private String content;
    private String commentDate;
    private String userName;
    private String headpicture;
    //评论者的id
    private Integer userId;
    //id为评论的id
    private Integer id;
    //文章id
    private Integer articleId;

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public CommentUser(String content, String commentDate, String userName, String headpicture, Integer userId, Integer id, Integer articleId) {
        this.content = content;
        this.commentDate = commentDate;
        this.userName = userName;
        this.headpicture = headpicture;
        this.userId = userId;
        this.id = id;
        this.articleId = articleId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
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
