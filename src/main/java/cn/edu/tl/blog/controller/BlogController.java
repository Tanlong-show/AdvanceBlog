package cn.edu.tl.blog.controller;

import cn.edu.tl.blog.entity.*;
import cn.edu.tl.blog.repository.*;
import cn.edu.tl.blog.service.ArticleServiceimpl;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/blog")
public class BlogController {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UsercollectRepository usercollectRepository;
    @Autowired
    private ArticleServiceimpl articleServiceimpl;
    @Autowired
    private RecommendRepository recommendRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private SearchRepository searchRepository;


    //编辑页面跳转
    @RequestMapping("/toWrite")
    public String to4(){
        return "to4";
    }

    @RequestMapping("/write")
    public ModelAndView write(Model model, HttpSession session){
        List<String> typeList = new ArrayList<>();
        typeList.add("JAVA");
        typeList.add("C/C++");
        typeList.add("PYTHON");
        typeList.add("PTA题集");
        typeList.add("硬件操作");
        typeList.add("LINUX");
        typeList.add("HTML/CSS");
        typeList.add("其它");
        model.addAttribute("typeList",typeList);
        return new ModelAndView("write","modelList",model);
    }
    //显示文章内容
    @RequestMapping("/content/to")
    public ModelAndView to(Model model, HttpSession session){
        User user = (User)session.getAttribute("user");
        Article article = (Article)session.getAttribute("article");
        model.addAttribute("commentuser",commentRepository.findComment(article.getId()));
        model.addAttribute("article",article);
        model.addAttribute("user",user);
        int action = (int)session.getAttribute("action");
        model.addAttribute("action",action);
        model.addAttribute("size",commentRepository.findComment(article.getId()).size());
        //文章作者
        User writer = userRepository.findUserById(article.getUserId());
        model.addAttribute("writer",writer);

        //回复的list
        List<Answer> answerList = new ArrayList<>();
        answerList = answerRepository.findByArticleId(article.getId());
        model.addAttribute("answerList",answerList);

        return new ModelAndView("article","articleModel",model);
    }
    @RequestMapping("/content/{id}")
    public String content(@PathVariable("id") Integer id, Model model, HttpSession session){
        Article article = articleRepository.getOne(id);
        System.out.println("Article:"+article.getTitle());
        session.setAttribute("article",article);
        //判断是否收藏过此文章,并做标记
        User user = (User)session.getAttribute("user");
        Usercollect usercollect = (Usercollect)usercollectRepository.findByUserIdAndArticleId(user.getId(),article.getId());
        int action = 0;
        if(usercollect != null){
            //说明收藏过
            action = 1;
        }
        session.setAttribute("action",action);

        return "to6";
    }


    //从浏览博客主页跳转
    @RequestMapping("/content2/{id}")
    public ModelAndView content2(@PathVariable("id") Integer id, Model model, HttpSession session){

        User user = (User)session.getAttribute("user");
        Article article = articleRepository.getOne(id);
        model.addAttribute("commentuser",commentRepository.findComment(article.getId()));
        model.addAttribute("size",commentRepository.findComment(article.getId()).size());
        model.addAttribute("article",article);
        model.addAttribute("user",user);
        //判断是否收藏过此文章,并做标记
        Usercollect usercollect = (Usercollect)usercollectRepository.findByUserIdAndArticleId(user.getId(),article.getId());
        int action = 0;
        if(usercollect != null){
            //说明收藏过
            action = 1;
        }
        model.addAttribute("action",action);
        //文章作者
        User writer = userRepository.findUserById(article.getUserId());
        model.addAttribute("writer",writer);
        //回复的list
        List<Answer> answerList = new ArrayList<>();
        answerList = answerRepository.findByArticleId(article.getId());
        model.addAttribute("answerList",answerList);
        return new ModelAndView("article2","articleModel",model);
    }


    //博客保存
    @RequestMapping("/save")
    public ModelAndView save(Model model,@RequestParam("file")MultipartFile file, HttpSession session,HttpServletRequest request, Article article) throws IOException, ParseException {

        User user = (User) session.getAttribute("user");
        article.setUserId(user.getId());

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String startTime = sdf.format(date);
        article.setArticleDate(startTime);

//        article.setContent(StringEscapeUtils.escapeXml(article.getContent()));
        // 1.保存图片的路径，图片上传成功后，将路径保存到数据库
        File ff = new File(System.getProperty("user.dir"));
        String filePath = ff.getParent()+"www/wwwroot/SpringBootProject/cover";
//        System.out.println("filepath:"+filePath);
        // 2.获取原始图片的名字
        String originalFilename = file.getOriginalFilename();
        //如果图片上传
        if(!originalFilename.equals("")){
            // 3.生成文件新的名字
            String newFileName = UUID.randomUUID() + originalFilename;
            // 4.封装上传文件位置的全路径
            File targetFile = new File(filePath, newFileName);
            file.transferTo(targetFile);
//            System.out.println("newFileName:"+newFileName);
//            System.out.println("targetFile:"+targetFile);
            article.setCover(newFileName);
        }else{
            article.setCover("1.jpg");
        }
        article.setComments(0);
        article.setPageLikes(0);
        article.setPageViews(0);
        article.setCollects(0);
        article.setRecommends(0);
        articleRepository.save(article);

        int id = user.getId();
        model.addAttribute("articleList1",articleRepository.findArticlesByTypeAndUserId("JAVA",id));
        model.addAttribute("articleList2",articleRepository.findArticlesByTypeAndUserId("C/C++",id));
        model.addAttribute("articleList3",articleRepository.findArticlesByTypeAndUserId("PYTHON",id));
        model.addAttribute("articleList4",articleRepository.findArticlesByTypeAndUserId("PTA题集",id));
        model.addAttribute("articleList5",articleRepository.findArticlesByTypeAndUserId("硬件操作",id));
        model.addAttribute("articleList6",articleRepository.findArticlesByTypeAndUserId("LINUX",id));
        model.addAttribute("articleList7",articleRepository.findArticlesByTypeAndUserId("HTML/CSS",id));
        model.addAttribute("articleList8",articleRepository.findArticlesByTypeAndUserId("其它",id));

        //写一篇博客+50经验
        user.setGrade(user.getGrade() + 50);
        userRepository.save(user);
        session.setAttribute("user", user);
        return new ModelAndView("myblog","articleModel",model);
    }

    //跳转到修改博客
    @RequestMapping("/toto/{id}")
    public String toto(HttpSession session, @PathVariable("id")Integer id){
        session.setAttribute("id",id);
        return "to7";
    }
    @RequestMapping("/toto/tomodify")
    public ModelAndView tomodify(Model model, HttpSession session){
        Integer id = (Integer)session.getAttribute("id");
        Article article = articleRepository.findArticleById(id);
        model.addAttribute("article",article);
        List<String> typeList = new ArrayList<>();
        typeList.add("JAVA");
        typeList.add("C/C++");
        typeList.add("PYTHON");
        typeList.add("PTA题集");
        typeList.add("硬件操作");
        typeList.add("LINUX");
        typeList.add("HTML/CSS");
        typeList.add("其它");
        model.addAttribute("typeList",typeList);
        return new ModelAndView("modifyblog","blogModel",model);
    }
    //删除博客
    @RequestMapping("/deleteArticle/{id}")
    public ModelAndView delete(Model model,@PathVariable("id")Integer id){
        articleRepository.deleteById(id);
        //也要删掉本文章的所有评论/收藏/推文
        commentRepository.deleteByArticleId(id);
        usercollectRepository.deleteByArticleId(id);
        recommendRepository.deleteByArticleId(id);
        return new ModelAndView("redirect:/index/myblog");
    }

    //管理端删除自己博客
    @RequestMapping("/deleteArticle2/{id}")
    public ModelAndView delete2(Model model,@PathVariable("id")Integer id){
        //也要删掉本文章的所有评论/收藏/推文
        answerRepository.deleteByArticleId(id);
        commentRepository.deleteByArticleId(id);
        usercollectRepository.deleteByArticleId(id);
        recommendRepository.deleteByArticleId(id);
        articleRepository.deleteById(id);
        return new ModelAndView("redirect:/user/blogmanage");
    }


    //博客修改后保存
    @RequestMapping("/modifysave")
    public String modify(@RequestParam("cover2")String cover2, Model model,@RequestParam("file")MultipartFile file, HttpSession session,HttpServletRequest request, Article article) throws IOException, ParseException {

        User user = (User) session.getAttribute("user");
        article.setUserId(user.getId());


//        article.setContent(StringEscapeUtils.escapeXml(article.getContent()));
        // 1.保存图片的路径，图片上传成功后，将路径保存到数据库
        File ff = new File(System.getProperty("user.dir"));
        String filePath = ff.getParent()+"www/wwwroot/SpringBootProject/cover";
//        System.out.println("filepath:"+filePath);
        // 2.获取原始图片的名字
        String originalFilename = file.getOriginalFilename();
        //如果图片上传
        if(!originalFilename.equals("")){
            // 3.生成文件新的名字
            String newFileName = UUID.randomUUID() + originalFilename;
            // 4.封装上传文件位置的全路径
            File targetFile = new File(filePath, newFileName);
            file.transferTo(targetFile);
//            System.out.println("newFileName:"+newFileName);
//            System.out.println("targetFile:"+targetFile);
            article.setCover(newFileName);
        }else{
            article.setCover(cover2);
        }

        articleRepository.save(article);

//        int id = user.getId();
//        model.addAttribute("articleList1",articleRepository.findArticlesByTypeAndUserId("JAVA",id));
//        model.addAttribute("articleList2",articleRepository.findArticlesByTypeAndUserId("C/C++",id));
//        model.addAttribute("articleList3",articleRepository.findArticlesByTypeAndUserId("PYTHON",id));
//        model.addAttribute("articleList4",articleRepository.findArticlesByTypeAndUserId("PTA题集",id));
//        model.addAttribute("articleList5",articleRepository.findArticlesByTypeAndUserId("硬件操作",id));
//        model.addAttribute("articleList6",articleRepository.findArticlesByTypeAndUserId("LINUX",id));
//        model.addAttribute("articleList7",articleRepository.findArticlesByTypeAndUserId("HTML/CSS",id));
//        model.addAttribute("articleList8",articleRepository.findArticlesByTypeAndUserId("其它",id));


        return "redirect:/user/blogmanage";
    }



    //查看他人博客
    @RequestMapping("/{userId}")
    public ModelAndView myblog(HttpSession session, Model model, @PathVariable("userId") Integer userId){
        session.setAttribute("otherblog",userId);
        model.addAttribute("articleList1",articleRepository.findArticlesByTypeAndUserId("JAVA",userId));
        model.addAttribute("articleList2",articleRepository.findArticlesByTypeAndUserId("C/C++",userId));
        model.addAttribute("articleList3",articleRepository.findArticlesByTypeAndUserId("PYTHON",userId));
        model.addAttribute("articleList4",articleRepository.findArticlesByTypeAndUserId("PTA题集",userId));
        model.addAttribute("articleList5",articleRepository.findArticlesByTypeAndUserId("硬件操作",userId));
        model.addAttribute("articleList6",articleRepository.findArticlesByTypeAndUserId("LINUX",userId));
        model.addAttribute("articleList7",articleRepository.findArticlesByTypeAndUserId("HTML/CSS",userId));
        model.addAttribute("articleList8",articleRepository.findArticlesByTypeAndUserId("其它",userId));
        return new ModelAndView("myblog","articleModel",model);
    }

    //评论保存
    @RequestMapping("/savesay")
    public void save(@RequestParam("content")String content,@RequestParam("articleId")Integer articleId,@RequestParam("date")String date,Model model,HttpSession session){
        User user = (User)session.getAttribute("user");
        //给文章评论+50经验
        user.setGrade(user.getGrade() + 50);
        userRepository.save(user);
        session.setAttribute("user", user);
        
        int id = user.getId();
        Comment comment = new Comment();
        comment.setUserId(id);
        comment.setArticleId(articleId);
        comment.setCommentDate(date);
        comment.setContent(content);
        //将本博客评论数+1
        Article article = articleRepository.findArticleById(articleId);
        article.setComments(article.getComments()+1);
        articleRepository.save(article);
        commentRepository.save(comment);
        session.setAttribute("article",article);
    }

    //搜索博客
    @RequestMapping("/searchBlog")
    public ModelAndView searchBlog(HttpSession session, Model model, @RequestParam("keyword")String keyword, @RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "size", defaultValue = "4") Integer size){

        User user = (User)session.getAttribute("user");
        Search search = new Search();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String startTime = sdf.format(date);
        search.setContent(keyword);
        search.setDate(startTime);
        search.setUserId(user.getId());
        searchRepository.save(search);

        if(keyword.equals("<查询所有>")){
            keyword="";
        }
        Page<Article> articles = articleServiceimpl.getArticleWithPageByKeyWord(page, size, keyword);
        model.addAttribute("articleList", articles);
        if(keyword.equals("")){
            keyword="<查询所有>";
        }
        model.addAttribute("keyword",keyword);
        model.addAttribute("sum",articles.getTotalElements());
        return new ModelAndView("blogsearch","articlemodel",model);
    }





    //用户管理端搜索博客
    @RequestMapping("/managesearchBlog2")
    public ModelAndView managesearchBlog(HttpSession session, Model model, @RequestParam("keyword")String keyword){

        //前端框架里存在的bug，搜索第一次会重复且有逗号，如搜索java，传入后端是：java,java
//        int place = keyword.indexOf(",");
//        if(place != -1) {
//            keyword = keyword.substring(0, place);
//        }
        User user = (User) session.getAttribute("user");
        int userid = user.getId();
        System.out.println("userid:  "+userid);
        List<Article> articleList =
                articleRepository.findByContentContainingOrTitleContainingAndUserId(userid,keyword,keyword);
        for (int i = 0; i < articleList.size(); i++) {
            System.out.println(articleList.get(i).getTitle());
        }


        model.addAttribute("articleList", articleList);
        model.addAttribute("user",user);
        return new ModelAndView("blogmanage2","blogmanageModel",model);
    }
    //管理员管理端搜索博客
    @RequestMapping("/managesearchBlog")
    public ModelAndView managesearchBlog2(HttpSession session, Model model, @RequestParam("keyword")String keyword){
//        int place = keyword.indexOf(",");
//        if(place != -1) {
//            keyword = keyword.substring(0, place);
//        }
        List<Article> articleList = articleRepository.findByContentContainingOrTitleContaining(keyword,keyword);
        System.out.println(articleList.size());

        model.addAttribute("articleList", articleList);
        List<User> userList = new ArrayList<>();
        for(int i = 0; i < articleList.size(); i++){
            userList.add(userRepository.findUserById(articleList.get(i).getUserId()));
        }
        //summarize=name , cover = headpicture
        for(int j = 0; j < userList.size(); j++){
            articleList.get(j).setSummarize(userList.get(j).getName());
            articleList.get(j).setCover(userList.get(j).getHeadpicture());
        }
        return new ModelAndView("blogmanage","blogmanageModel",model);
    }





    //显示某种类别的博客
    @RequestMapping("/type")
    public ModelAndView type(Model model,@RequestParam("type")String type,@RequestParam(value = "size", defaultValue = "4") Integer size,
                             @RequestParam(value = "page",defaultValue = "1") Integer page){
        Page<Article> articles = articleServiceimpl.getArticleByType(page,size,type);
        model.addAttribute("articleList", articles);
        if(type.equals("C/C  ")){
            type = "C/C++";
        }
        model.addAttribute("type", type);
        System.out.println(type);
        model.addAttribute("sum",articles.getTotalElements());
        return new ModelAndView("blogtype","articlemodel",model);
    }

    //显示某种类别的博客
    @ResponseBody
    @RequestMapping("/showKeyword/{keyword}")
    public List<Article> showkey(@PathVariable("keyword")String keyword){
        List<Article> articles = articleRepository.findByContentContainingOrTitleContaining(keyword, keyword);
        return articles;
    }

    //根据日期找到指定的博客
    @RequestMapping("/dateblog")
    @ResponseBody
    public List<UserArticleCollect> datablog(String date,HttpSession session){
        User user = (User)session.getAttribute("user");
        List<Usercollect> usercollectList = usercollectRepository.findAllByColtimeContainingAndUserId(date,user.getId());
        List<Article> articleList = new ArrayList<>();
        for(int i = 0; i < usercollectList.size(); i++) {
            articleList.add(articleRepository.findArticleById(usercollectList.get(i).getArticleId()));
        }
        List<User> userList = new ArrayList<>();
        for(int i = 0; i < usercollectList.size(); i++) {
            userList.add(userRepository.findUserById(articleList.get(i).getUserId()));
        }

        List<UserArticleCollect> userArticleCollectList = new ArrayList<>();
        for(int i = 0; i < usercollectList.size(); i++){
            UserArticleCollect a = new UserArticleCollect();
            a.setArticleId(articleList.get(i).getId());
            a.setCollecttime(usercollectList.get(i).getColtime());
            a.setHeadpicture(userList.get(i).getHeadpicture());
            a.setName(userList.get(i).getName());
            a.setOuttime(articleList.get(i).getArticleDate());
            a.setTitle(articleList.get(i).getTitle());
            a.setUserId(userList.get(i).getUserId());
            a.setGood(articleList.get(i).getPageLikes());
            a.setComment(articleList.get(i).getComments());
            a.setCover(articleList.get(i).getCover());
            userArticleCollectList.add(a);
        }
        return userArticleCollectList;
    }

}
