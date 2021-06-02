package cn.edu.tl.blog.controller;

import cn.edu.tl.blog.entity.Article;
import cn.edu.tl.blog.entity.Friendship;
import cn.edu.tl.blog.entity.User;
import cn.edu.tl.blog.entity.Usercollect;
import cn.edu.tl.blog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private UsercollectRepository usercollectRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private RecommendRepository recommendRepository;

    //联系开发团队
    @RequestMapping("/contactUs")
    public String contactUs(){
        return "contactUs";
    }

    //通过学号查找人
    @RequestMapping("/{userId}")
    public ModelAndView findByuserId(Model model, @PathVariable("userId") Integer userId){
        System.out.println(userId);
        User user = userRepository.findByUserId(userId);
        model.addAttribute("user",user);
        System.out.println(user.getHeadpicture());
        return new ModelAndView("peopleInformation","userModel",model);
    }

    //删好友
    @RequestMapping("/delete/{friendId}")
    public void delete(HttpSession session, @PathVariable("friendId") Integer friendId, Model model){
        User user = (User)session.getAttribute("user");
        friendshipRepository.deleteByUserIdIsAndFriendIdIs(user.getId(), friendId);
        friendshipRepository.deleteByUserIdIsAndFriendIdIs(friendId, user.getId());
//        return new ModelAndView("redirect:/login/go");
    }

    //加好友
    @RequestMapping("/add/{friendId2}")
    public String add(HttpSession session, @PathVariable("friendId2") Integer friendId, Model model){
        System.out.println("Add friendIdL: "+friendId);
        User user = (User)session.getAttribute("user");
        Friendship friendship = new Friendship();
        friendship.setFriendId(friendId);
        friendship.setUserId(user.getId());
        if(friendshipRepository.findByUserIdAndAndFriendId(user.getId(),friendId)==null){
            friendshipRepository.save(friendship);
        }

        Friendship friendship2 = new Friendship();
        friendship2.setFriendId(user.getId());
        friendship2.setUserId(friendId);
        if(friendshipRepository.findByUserIdAndAndFriendId(friendId,user.getId())==null){
            friendshipRepository.save(friendship2);
        }

        return "redirect:/login/toIndex";
    }



    //关键字查找用户
    @RequestMapping("/searchpageList")
    @ResponseBody
    public List<User> article_searchpageList(@RequestParam("keyWord") String keyword, HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        model.addAttribute("keyword",keyword);
        /*通过关键词找出的user*/
        List<User> list = new ArrayList<>();
        list = userRepository.findUsersByNameLike(keyword);

        return list;
    }

    //to
    @RequestMapping("/toto")
    public String mycollect(){
        return "to14";
    }
    //我的收藏
    @RequestMapping("/mycollect")
    public ModelAndView mycollect(Model model,HttpSession session){
        User user = (User)session.getAttribute("user");

        List<Usercollect> usercollectList = usercollectRepository.findAllByUserId(user.getId());
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
        model.addAttribute("userArticleCollectList",userArticleCollectList);

        return new ModelAndView("mycollect","mycollectModel",model);
    }
 //state改为1
    @RequestMapping("/searchpageList1")
    @ResponseBody
    public String article_searchpageList1(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user!=null){
            user.setState(1);
            userRepository.save(user);
        }
        return "66";
    }
    //state改为0
    @RequestMapping("/searchpageList2")
    @ResponseBody
    public String article_searchpageList2(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user != null) {
            user.setState(0);
            userRepository.save(user);
        }
        return "66";
    }

    @RequestMapping("/blogmanageto")
    public ModelAndView blogmanageto(Model model, HttpSession session){
        return new ModelAndView("to9");
    }
    @RequestMapping("/blogmanage")
    public ModelAndView blogmanage(Model model, HttpSession session){
        List<Article> articleList = articleRepository.findAll();
        model.addAttribute("articleList",articleList);
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

    @RequestMapping("/blogmanage2to")
    public ModelAndView blogmanage2to(Model model, HttpSession session){
        return new ModelAndView("to10");
    }
    @RequestMapping("/blogmanage2")
    public ModelAndView blogmanage2(Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        List<Article> articleList = articleRepository.findArticlesByUserId(user.getId());
        model.addAttribute("articleList",articleList);
        model.addAttribute("user",user);
        return new ModelAndView("blogmanage2","blogmanageModel",model);
    }

    //用户管理，删除用户
    @RequestMapping("/deleteuser")
    @ResponseBody
    public boolean deleteuser(Integer userId){
        answerRepository.deleteByUserId(userId);//删除回复
        commentRepository.deleteByUserId(userId);//删除文章评论
        articleRepository.deleteArticleByUserId(userId);//删除文章
        friendshipRepository.deleteByUserId(userId);//删除好友
        friendshipRepository.deleteByFriendId(userId);
        recommendRepository.deleteByUserId(userId);//删除推文
        usercollectRepository.deleteByArticleId(userId);  //删除收藏文章
        userRepository.deleteById(userId);//删除用户

        return true;
    }

}
