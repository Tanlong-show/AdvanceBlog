package cn.edu.tl.blog.controller;

import cn.edu.tl.blog.entity.*;
import cn.edu.tl.blog.repository.*;
import cn.edu.tl.blog.service.ContentService;
import cn.edu.tl.blog.utils.RedisUtil;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/index")
public class IndexController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private RecommendRepository recommendRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private ContentService contentService;
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;
    @Autowired
    private RedisUtil redisUtil;

    //多图统计跳转
    @RequestMapping("/go2")
    public ModelAndView to3() {
        return new ModelAndView("to3");
    }
    @RequestMapping("/toChart")
    public ModelAndView toChart(HttpSession session, Model model){
        return new ModelAndView("chart","chartModel",model);
    }

    //ajax请求数组——文章类别占比
    @RequestMapping("liuliang")
    @ResponseBody
    public int[] liuliang(){
        int []a = new int[12];
        int sum = 0, cut = 0;
        List<Comment> commentList = new ArrayList<>();
        commentList = commentRepository.findAll();
        for(int i = 0; i < commentList.size(); i++){
            commentList.get(i).setCommentDate(commentList.get(i).getCommentDate().substring(5,10));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Date today = new Date();//获取今天的日期

        int z = 0;
        for(int j = 0; j < 5; j++){
            int po = sum;
            for(int i = 0; i < commentList.size(); i++){
                String startTime = sdf.format(today);
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                c.add(Calendar.DAY_OF_MONTH, j-4);
                Date day = c.getTime();//这是昨天
                startTime = sdf.format(day);
                if(commentList.get(i).getCommentDate().equals(startTime)){
                    sum++;
                }
            }
            cut = sum - po;
            a[z++] = sum;
            a[z++] = cut;
        }
        for(int i = 0; i < 10; i++){
            System.out.print(a[i]+" ");
        }

        return a;
    }

    @RequestMapping("datelist")
    @ResponseBody
    public List<String> datelist(){

        List<String> a = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");

        Date today = new Date();//获取今天的日期
        String startTime = sdf.format(today);
        a.add(startTime);


        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = c.getTime();//这是昨天
        startTime = sdf.format(yesterday);
        a.add(startTime);

        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -2);
        Date yesterday1 = c.getTime();//这是前天
        startTime = sdf.format(yesterday1);
        a.add(startTime);

        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -3);
        Date yesterday2 = c.getTime();//这是大前天
        startTime = sdf.format(yesterday2);
        a.add(startTime);

        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -4);
        Date yesterday3 = c.getTime();//这是大大前天
        startTime = sdf.format(yesterday3);
        a.add(startTime);
        return a;
    }


    //ajax请求数组——文章类别占比
    @RequestMapping("catagory")
    @ResponseBody
    public int[] catagory(){
        int []a = new int[8];
        List<Article> articleList = articleRepository.findAll();
        for(int i = 0; i < articleList.size(); i++){
            if(articleList.get(i).getType().equals("JAVA")){
                a[0]++;
            }
            else if(articleList.get(i).getType().equals("C/C++")){
                a[1]++;
            }
            else if(articleList.get(i).getType().equals("PYTHON")){
                a[2]++;
            }
            else if(articleList.get(i).getType().equals("PTA题集")){
                a[3]++;
            }
            else if(articleList.get(i).getType().equals("硬件操作")){
                a[4]++;
            }
            else if(articleList.get(i).getType().equals("LINUX")){
                a[5]++;
            }
            else if(articleList.get(i).getType().equals("HTML/CSS")){
                a[6]++;
            }
            else{
                //其它
                a[7]++;
            }
        }
        return a;
    }

    //ajax请求数组——不同专业人数数据
    @RequestMapping("speciality")
    @ResponseBody
    public int[] speciality(){
        //获取不同专业人数数据
        List<User> list = userRepository.findAll();
        int []speciality = new int[5];

        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getSpeciality().equals("软件工程")){
                speciality[0]++;
            }
            else if(list.get(i).getSpeciality().equals("物联网工程")){
                speciality[2]++;
            }
            else if(list.get(i).getSpeciality().equals("电子信息工程")){
                speciality[3]++;
            }
            else if(list.get(i).getSpeciality().equals("计算机科学与技术")){
                speciality[1]++;
            }
            else{
                //其它
                speciality[4]++;
            }
        }
        return speciality;
    }

    //ajax请求数组——不同专业人pk
    @RequestMapping("pk")
    @ResponseBody
    public int[] pk(){
        int a[] = new int[10];

        List<ArticleUser> ruanjian = articleRepository.selectpk("软件工程");
        List<ArticleUser> jisuanji = articleRepository.selectpk("计算机科学与技术");
        List<ArticleUser> wulianwang = articleRepository.selectpk("物联网工程");
        List<ArticleUser> dianzi = articleRepository.selectpk("电子信息工程");
        List<ArticleUser> qita = articleRepository.selectpk("其它");


        for(int i = 0; i < ruanjian.size(); i++){
            a[0] += ruanjian.get(i).getCollects();
            a[5] += ruanjian.get(i).getRecommends();
        }
        for(int i = 0; i < jisuanji.size(); i++){
            a[1] += jisuanji.get(i).getCollects();
            a[6] += jisuanji.get(i).getRecommends();
        }
        for(int i = 0; i < wulianwang.size(); i++){
            a[2] += wulianwang.get(i).getCollects();
            a[7] += wulianwang.get(i).getRecommends();
        }
        for(int i = 0; i < dianzi.size(); i++){
            a[3] += dianzi.get(i).getCollects();
            a[8] += dianzi.get(i).getRecommends();
        }
        for(int i = 0; i < qita.size(); i++){
            a[4] += qita.get(i).getCollects();
            a[9] += qita.get(i).getRecommends();
        }

        return a;
    }



    //资助跳转
    @RequestMapping("/money")
    public String money(){
        return "money";
    }

    @RequestMapping("/to")
    public String to(){
        return "to2";
    }

    //我的博客。个人博客
    @RequestMapping("/myblog")
    public ModelAndView toBlog(Model model, HttpSession session){
        User user = (User)session.getAttribute("user");
        int id = user.getId();
        model.addAttribute("articleList1",articleRepository.findArticlesByTypeAndUserId("JAVA",id));
        model.addAttribute("articleList2",articleRepository.findArticlesByTypeAndUserId("C/C++",id));
        model.addAttribute("articleList3",articleRepository.findArticlesByTypeAndUserId("PYTHON",id));
        model.addAttribute("articleList4",articleRepository.findArticlesByTypeAndUserId("PTA题集",id));
        model.addAttribute("articleList5",articleRepository.findArticlesByTypeAndUserId("硬件操作",id));
        model.addAttribute("articleList6",articleRepository.findArticlesByTypeAndUserId("LINUX",id));
        model.addAttribute("articleList7",articleRepository.findArticlesByTypeAndUserId("HTML/CSS",id));
        model.addAttribute("articleList8",articleRepository.findArticlesByTypeAndUserId("其它",id));
        return new ModelAndView("myblog","articleModel",model);
    }
    //他人博客返回跳转他人博客主页
    @RequestMapping("/otherblog")
    public ModelAndView otherblog(Model model, HttpSession session){
        int id = 0;
        if(session.getAttribute("otherblog") == null){
            User user = (User)session.getAttribute("user");
            id = user.getId();
        }else{
            id = (Integer)session.getAttribute("otherblog");
            session.setAttribute("otherblog",null);
        }
        model.addAttribute("articleList1",articleRepository.findArticlesByTypeAndUserId("JAVA",id));
        model.addAttribute("articleList2",articleRepository.findArticlesByTypeAndUserId("C/C++",id));
        model.addAttribute("articleList3",articleRepository.findArticlesByTypeAndUserId("PYTHON",id));
        model.addAttribute("articleList4",articleRepository.findArticlesByTypeAndUserId("PTA题集",id));
        model.addAttribute("articleList5",articleRepository.findArticlesByTypeAndUserId("硬件操作",id));
        model.addAttribute("articleList6",articleRepository.findArticlesByTypeAndUserId("LINUX",id));
        model.addAttribute("articleList7",articleRepository.findArticlesByTypeAndUserId("HTML/CSS",id));
        model.addAttribute("articleList8",articleRepository.findArticlesByTypeAndUserId("其它",id));
        return new ModelAndView("myblog","articleModel",model);
    }



    @RequestMapping("/go")
    public ModelAndView to2(HttpSession session,Model model) {
        return new ModelAndView("to2","userModel",model);
    }

    //修改个人信息
    @RequestMapping("/modify")
    public ModelAndView modify(Model model, HttpSession session){
        User user = (User)session.getAttribute("user");
        model.addAttribute("user",user);
        return new ModelAndView("modifyuser","userModel",model);
    }

    //管理员修改后保存个人信息

    @RequestMapping("/managesave")
    public ModelAndView managesave(HttpServletRequest request, Model model, HttpSession session, User user, MultipartFile file) throws IOException {

        File ff = new File(System.getProperty("user.dir"));
        String filePath = ff.getParent()+"www/wwwroot/SpringBootProject/headpicture";

        // 2.获取原始图片的名字
        String originalFilename = file.getOriginalFilename();
        //如果图片上传
        if(!originalFilename.equals("")){
            // 3.生成文件新的名字
            String newFileName = UUID.randomUUID() + originalFilename;
            // 4.封装上传文件位置的全路径
            File targetFile = new File(filePath, newFileName);
            file.transferTo(targetFile);
            user.setHeadpicture(newFileName);
        }else{
            user.setHeadpicture(user.getHeadpicture());
        }
        userRepository.save(user);

        //至此，管理员修改的user结束
        //user1为登陆的用户本身
        User user1 = (User)session.getAttribute("user");
        model.addAttribute("user",user1);
        session.setAttribute("user",user1);
        //查找好友
        List<Friendship> list2 = friendshipRepository.findAllByUserId(user1.getId());
        List<User> list = new ArrayList<>();
        for(int i = 0; i < list2.size(); i++){
            list.add(userRepository.findUserById(list2.get(i).getFriendId()));
        }
        model.addAttribute("list", list);
        return new ModelAndView("redirect:/login/go","userModel",model);
    }

    //保存个人信息
    @RequestMapping("/save")
    public ModelAndView save(HttpServletRequest request, Model model, HttpSession session, User user, MultipartFile file) throws IOException {

        File ff = new File(System.getProperty("user.dir"));
        String filePath = ff.getParent()+"www/wwwroot/SpringBootProject/headpicture";


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

            user.setHeadpicture(newFileName);
        }else{
            user.setHeadpicture(user.getHeadpicture());
        }

        model.addAttribute("user",user);
        userRepository.save(user);
        session.setAttribute("user",user);
        //查找好友
        List<Friendship> list2 = friendshipRepository.findAllByUserId(user.getId());
        List<User> list = new ArrayList<>();
        for(int i = 0; i < list2.size(); i++){
            list.add(userRepository.findUserById(list2.get(i).getFriendId()));
        }
        model.addAttribute("list", list);
        return new ModelAndView("redirect:/login/go","userModel",model);
    }

    //每日推文
    @RequestMapping("/recommend")
    public String recommend(){
        return "to8";
    }
    @RequestMapping("/torecommend")
    public ModelAndView torecommend(Model model){
        Date date = new Date();
        //推文只记当前日期,不精确到时分秒
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowtime = sdf.format(date);
        model.addAttribute("nowtime",nowtime);
        List<ArticleUser> articleUserList = new ArrayList<>();
        List<Recommend> recommendList = recommendRepository.findAll();
        for(int i = 0; i < recommendList.size(); i++){
            if(recommendList.get(i).getRectime().equals(nowtime)){
                articleUserList.add(articleRepository.recommendarticleUser(recommendList.get(i).getUserId(), recommendList.get(i).getArticleId()));
            }
        }
        model.addAttribute("articleUserList",articleUserList);
        return new ModelAndView("recommend","recommendModel",model);
    }



    //开发人员介绍
    @RequestMapping("/peopleIntroduce")
    public ModelAndView toIntroduce(Model model){
        return new ModelAndView("peopleIntroduce");
    }

    //跳转博客主页
    @RequestMapping("/blogindex")
    public ModelAndView blogindex(Model model){
        List<ArticleUser> articleUserList10 = new ArrayList<>();
        List<ArticleUser> articleUserList11 = new ArrayList<>();

        List<ArticleUser> articleUserList1 = articleRepository.articleUser();
        List<ArticleUser> articleUserList2 = new ArrayList<>();//按收藏排列
        List<ArticleUser> articleUserList3 = new ArrayList<>();//按点赞排列
        List<ArticleUser> articleUserList4 = new ArrayList<>();//按评论排列
        List<ArticleUser> articleUserList5 = new ArrayList<>();//按浏览量排列
        List<ArticleUser> articleUserList6 = new ArrayList<>();//按推文排列

        if(redisUtil.hasKey("articlecollects") && redisUtil.hasKey("articlerecommend1")){

            //从redis读取数据
            articleUserList2 = (List<ArticleUser>) redisUtil.get("articlecollects");
            articleUserList3 = (List<ArticleUser>)redisUtil.get("articlelikes");
            articleUserList4.add((ArticleUser)redisUtil.get("articlecomments0"));
            articleUserList10 = (List<ArticleUser>)redisUtil.get("articlecomments12");
            articleUserList5 = (List<ArticleUser>)redisUtil.get("articleviews");
            articleUserList11 = (List<ArticleUser>)redisUtil.get("articlerecommend");
            articleUserList6 = (List<ArticleUser>)redisUtil.get("articlerecommend1");

            model.addAttribute("articlecollects",articleUserList2);
            model.addAttribute("articlelikes",articleUserList3);
            model.addAttribute("articlecomments0",articleUserList4.get(0));
            model.addAttribute("articlecomments12",articleUserList10);
            model.addAttribute("articleviews",articleUserList5);
            model.addAttribute("articlerecommend",articleUserList11);//前3篇
            model.addAttribute("articlerecommend1",articleUserList6);//前5篇
            return new ModelAndView("blogindex","articlemodel",model);
        }



        int n = articleUserList1.size();
        int a = 0;
        //找到前三位收藏量最高的博客
        for(int i = 0;i<n;++i){
            a = i;
            for(int j = i+1;j<n;++j){
                if(articleUserList1.get(j).getCollects()>articleUserList1.get(a).getCollects()){
                    a = j;
                }
            }
            ArticleUser aa = new ArticleUser(articleUserList1.get(i));
            ArticleUser bb = new ArticleUser(articleUserList1.get(a));
            articleUserList1.set(i,bb);
            articleUserList1.set(a,aa);
            ArticleUser cc = new ArticleUser(articleUserList1.get(i));
            articleUserList2.add(cc);
            if(i==2) {
                break;
            }
        }




        //找到前三位点赞量最高的博客
        for(int i = 0;i<n;++i){
            a = i;
            for(int j = i+1;j<n;++j){
                if(articleUserList1.get(j).getPageLikes()>articleUserList1.get(a).getPageLikes()){
                    a = j;
                }
            }
            ArticleUser aa = new ArticleUser(articleUserList1.get(i));
            ArticleUser bb = new ArticleUser(articleUserList1.get(a));
            articleUserList1.set(i,bb);
            articleUserList1.set(a,aa);
            ArticleUser cc = new ArticleUser(articleUserList1.get(i));
            articleUserList3.add(cc);
            if(i==2) {
                break;
            }
        }


        //找到前三位评论最高的博客
        for(int i = 0;i<n;++i){
            a = i;
            for(int j = i+1;j<n;++j){
                if(articleUserList1.get(j).getComments()>articleUserList1.get(a).getComments()){
                    a = j;
                }
            }
            ArticleUser aa = new ArticleUser(articleUserList1.get(i));
            ArticleUser bb = new ArticleUser(articleUserList1.get(a));
            articleUserList1.set(i,bb);
            articleUserList1.set(a,aa);
            ArticleUser cc = new ArticleUser(articleUserList1.get(i));
            articleUserList4.add(cc);
            if(i==2) {
                break;
            }
        }


        articleUserList10.add(articleUserList4.get(1));
        articleUserList10.add(articleUserList4.get(2));
        //找到前三位浏览量最高的博客
        for(int i = 0;i<n;++i){
            a = i;
            for(int j = i+1;j<n;++j){
                if(articleUserList1.get(j).getPageViews()>articleUserList1.get(a).getPageViews()){
                    a = j;
                }
            }
            ArticleUser aa = new ArticleUser(articleUserList1.get(i));
            ArticleUser bb = new ArticleUser(articleUserList1.get(a));
            articleUserList1.set(i,bb);
            articleUserList1.set(a,aa);
            ArticleUser cc = new ArticleUser(articleUserList1.get(i));
            articleUserList5.add(cc);
            if(i==2) {
                break;
            }
        }
        //找到前五推文最高的博客
        for(int i = 0;i<n;++i){
            a = i;
            for(int j = i+1;j<n;++j){
                if(articleUserList1.get(j).getRecommends()>articleUserList1.get(a).getRecommends()){
                    a = j;
                }
            }
            ArticleUser aa = new ArticleUser(articleUserList1.get(i));
            ArticleUser bb = new ArticleUser(articleUserList1.get(a));
            articleUserList1.set(i,bb);
            articleUserList1.set(a,aa);
            ArticleUser cc = new ArticleUser(articleUserList1.get(i));
            articleUserList6.add(cc);
            if(i==4) {
                break;
            }
        }
        articleUserList11.add(articleUserList6.get(0));
        articleUserList11.add(articleUserList6.get(1));
        articleUserList11.add(articleUserList6.get(2));

        articleUserList6.get(0).setComments(1);
        articleUserList6.get(1).setComments(2);
        articleUserList6.get(2).setComments(3);
        articleUserList6.get(3).setComments(4);
        articleUserList6.get(4).setComments(5);
        model.addAttribute("articlecollects",articleUserList2);
        model.addAttribute("articlelikes",articleUserList3);
        model.addAttribute("articlecomments0",articleUserList4.get(0));
        model.addAttribute("articlecomments12",articleUserList10);
        model.addAttribute("articleviews",articleUserList5);
        model.addAttribute("articlerecommend",articleUserList11);//前3篇
        model.addAttribute("articlerecommend1",articleUserList6);//前5篇

        //redis存数据，并设置过期时间,5个小时自动更新一次
        redisUtil.set("articlecollects",articleUserList2,18000);
        redisUtil.set("articlelikes",articleUserList3,18000);
        redisUtil.set("articlecomments0",articleUserList4.get(0),18000);
        redisUtil.set("articlecomments12",articleUserList10,18000);
        redisUtil.set("articleviews",articleUserList5,18000);
        redisUtil.set("articlerecommend",articleUserList11,18000);
        redisUtil.set("articlerecommend1",articleUserList6,18000);
        return new ModelAndView("blogindex","articlemodel",model);
    }

    //等级排行
    @RequestMapping("level")
    public ModelAndView tolevel(HttpSession session, Model model)
    {
        User user = (User)session.getAttribute("user");
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(2);
        String process = nt.format(user.getGrade()%10000/10000.0);
        //进度条百分比
        model.addAttribute("process",process);
        model.addAttribute("user",user);
        int level = user.getGrade()/10000;
        model.addAttribute("level",level);

        //排名人，十个
        List<User> list = userRepository.findAll(Sort.by(Sort.Direction.DESC,"grade"));
        for(int i = 0; i < 10; i++){
            if(list.get(i).getHeadpicture()==null){
                list.get(i).setHeadpicture("201608311941005977.gif");
            }
        }
        model.addAttribute("list",list);
        return new ModelAndView("level","levelModel",model);
    }

    @RequestMapping("/otheranswer")
    public ModelAndView otheranswer(HttpSession session,Model model){
        User user = (User)session.getAttribute("user");
        //先查询该用户所有文章
        List<Article> articleList = articleRepository.findArticlesByUserId(user.getId());
        List<CommentUser> commentUserList = new ArrayList<>();
        List<Answer> answerList = new ArrayList<>();
        for(int i = 0; i < articleList.size(); i++){
            //文章下的评论
            List<CommentUser> a = new ArrayList<>();
            a = commentRepository.findComment(articleList.get(i).getId());
            for(int j = 0; j < a.size(); j++){
                commentUserList.add(a.get(j));
            }
            List<Answer>b = new ArrayList<>();
            b = answerRepository.findByArticleId(articleList.get(i).getId());
            //文章下的回复
            for(int z = 0 ; z < b.size(); z++){
                answerList.add(b.get(z));
            }
        }

        model.addAttribute("articleList",articleList);
        model.addAttribute("commentUserList",commentUserList);
        model.addAttribute("answerList",answerList);
        return new ModelAndView("otheranswer","answerModel",model);
    }

    //每日数据
    @RequestMapping("/data")
    public ModelAndView data(Model model, HttpSession session){
        return new ModelAndView("data");
    }

    //数据概览
    @RequestMapping("/fulldata")
    public ModelAndView fulldata(Model model, HttpSession session){

        int allpeople = userRepository.countAll();
        model.addAttribute("allpeople",allpeople);
        int greenpeople = userRepository.countgreen();
        model.addAttribute("greenpeople",greenpeople);
        double pressure = greenpeople*100.0/allpeople;
        String s = String.format("%.2f", pressure);
        model.addAttribute("pressure",s);
        return new ModelAndView("fulldata","fullModel",model);
    }

    //论坛入口
    @RequestMapping("/luntan")
    public ModelAndView luntan() {
        return new ModelAndView("luntan");
    }
    //论坛入口进入
    @RequestMapping("/luntan2/{keyword}")
    public ModelAndView luntan2(Model model,@PathVariable("keyword")String keyword) throws Exception {
        model.addAttribute("keyword",keyword);

        //ES爬取数据并展示(加sout输出即可展示true,这个方法会自动判断爬取过没有，爬取过就不再重复获取数据)
        if( contentService.parseContent(keyword, keyword)) {
            List<Map<String, Object>> hacontentList2 = contentService.searchPageHighlightBuilder(keyword, 1, 6);

            List<Hacontent> hacontentList = new ArrayList<>();

            for(int i = 0; i < hacontentList2.size(); i++){
                Hacontent hacontent = new Hacontent();
                    hacontent.setHref("" + hacontentList2.get(i).get("href"));
                    hacontent.setUsername("" + hacontentList2.get(i).get("username"));
                    hacontent.setContent("" + hacontentList2.get(i).get("content"));
                    hacontent.setDate("" + hacontentList2.get(i).get("date"));
                    if(hacontentList2.get(i).get("img").equals("")){
                        hacontent.setImg(null);
                    }
                    else{
                        hacontent.setImg(""+hacontentList2.get(i).get("img"));
                    }

                    hacontent.setTitle("" + hacontentList2.get(i).get("title"));
                    hacontentList.add(hacontent);
                }
            model.addAttribute("size",hacontentList.size());

            model.addAttribute("hacontentList",hacontentList);
        }

        //删除，减内存，下次更新
//        DeleteIndexRequest request = new DeleteIndexRequest("jd_goods");
//        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);

        return new ModelAndView("luntan2","forummodel",model);
    }

//    @RequestMapping("/popo")
//    @ResponseBody
//    public List<Map<String, Object>> popo() throws Exception {
////        contentService.parseContent("美");
//        List<Map<String, Object>> hacontentList = contentService.searchPage("王者");
//        return hacontentList;
//    }



    //版本通知
    @RequestMapping("/version")
    public ModelAndView version(Model model) {
        List<Version> versionList = new ArrayList<>();
        versionList = versionRepository.findAll();
        System.out.println(versionList.get(1).getContent());
        model.addAttribute("versionList",versionList);
        return new ModelAndView("version","versionModel",model);
    }


    @RequestMapping("/playtv")
    public ModelAndView playtv(Model model,HttpSession session) {
        User user = (User)session.getAttribute("user");
        model.addAttribute("user",user);
        return new ModelAndView("movie","movieModel",model);
    }

    //跳转至用户管理
    @RequestMapping("usermanageto")
    public ModelAndView usermanageto(Model model){
        return new ModelAndView("to11");
    }
    @RequestMapping("usermanage")
    public ModelAndView usermanage(Model model){
        model.addAttribute("users",userRepository.findAll());
        return new ModelAndView("usermanage","usermodel",model);
    }

    @RequestMapping("/go/{id}")
    public ModelAndView to12(HttpSession session,Model model,@PathVariable("id") Integer userId) {
        session.setAttribute("user2",userRepository.findUserById(userId));
        return new ModelAndView("to12");
    }
    @RequestMapping("usermanageto2")
    public ModelAndView usermanageto2(Model model,HttpSession session,@RequestParam("keyword") String keyword){
        session.setAttribute("keyword",keyword);
        return new ModelAndView("to13");
    }
    @RequestMapping("usermanage2")
    public ModelAndView usermanage2(Model model, HttpSession session){
        String keyword = (String) session.getAttribute("keyword");
        Integer keyword1 = 00000000;
        if(keyword.length() == keyword.getBytes().length){
            keyword1 = Integer.valueOf((String) session.getAttribute("keyword"));
        }
//        Integer keyword1 = (Integer)session.getAttribute("keyword");
        model.addAttribute("users",userRepository.findByNameContainingOrUserId(keyword,keyword1));
        return new ModelAndView("usermanage","usermodel",model);
    }
    //修改个人信息(根据id)
    @RequestMapping("/go/modify2")
    public ModelAndView modify2(Model model,HttpSession session){
        User user = (User)session.getAttribute("user2");
        model.addAttribute("user",user);
        return new ModelAndView("managemodifyuser","userModel",model);
    }

    //随心问答
    @RequestMapping("/suixin")
    public ModelAndView suixin(Model model,HttpSession session){
        session.removeAttribute("keywordbaidu");
        User user = (User)session.getAttribute("user2");
        model.addAttribute("user",user);
        return new ModelAndView("suixin","userModel",model);
    }
}
