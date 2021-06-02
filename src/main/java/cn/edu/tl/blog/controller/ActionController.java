package cn.edu.tl.blog.controller;

import cn.edu.tl.blog.entity.*;
import cn.edu.tl.blog.repository.*;
import cn.edu.tl.blog.utils.HtmlParseUtil;
import cn.edu.tl.blog.utils.SimilarUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/action")
public class ActionController {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UsercollectRepository usercollectRepository;
    @Autowired
    private RecommendRepository recommendRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private VideocommentRepository videocommentRepository;
    @Autowired
    private SearchRepository searchRepository;
    @RequestMapping("/addlook")
    public void addlook(@RequestParam("articleId")Integer articleId, HttpSession session){
        Article article = articleRepository.findArticleById(articleId);
        article.setPageViews(article.getPageViews()+1);
        articleRepository.save(article);
        session.setAttribute("article",article);
    }


    @RequestMapping("/addrecommend")
    public void addrecommend(@RequestParam("articleId")Integer articleId, HttpSession session){
        Article article = articleRepository.findArticleById(articleId);
        article.setRecommends(article.getRecommends()+1);
        articleRepository.save(article);

        //推文只记当前日期,不精确到时分秒
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowtime = sdf.format(date);
        User user = (User)session.getAttribute("user");
        Recommend recommend = new Recommend();
        recommend.setArticleId(articleId);
        recommend.setUserId(user.getId());
        recommend.setRectime(nowtime);
        recommendRepository.save(recommend);
        session.setAttribute("article",article);

    }

    @RequestMapping("/addlike")
    public void addlike(@RequestParam("articleId")Integer articleId,HttpSession session){
        Article article = articleRepository.findArticleById(articleId);
        article.setPageLikes(article.getPageLikes()+1);
        User user = (User)session.getAttribute("user");
        user.setGrade(user.getGrade()+50);
        userRepository.save(user);
        session.setAttribute("user",user);
        articleRepository.save(article);
        session.setAttribute("article",article);
    }

    //action用来判断是添加还是删除收藏
    @RequestMapping("/collect")
    public void collect(@RequestParam("articleId")Integer articleId, @RequestParam("action")Integer action, @RequestParam("date")String date,HttpSession session){
        User user = new User();
        user = (User)session.getAttribute("user");
        Usercollect usercollect = new Usercollect();
        Article article = articleRepository.findArticleById(articleId);

        if(action == 1){
            //添加收藏
            usercollect.setArticleId(articleId);
            usercollect.setUserId(user.getId());
            usercollect.setColtime(date);
            usercollectRepository.save(usercollect);
            article.setCollects(article.getCollects()+1);
            articleRepository.save(article);
            //自己收藏自己不加经验
            if(user.getId() != article.getUserId()) {
                User user2 = userRepository.findUserById(article.getUserId());
                user2.setGrade(user2.getGrade() + 100);
                userRepository.save(user2);
            }
        }else{
            //为0则删除收藏
            usercollectRepository.deleteByUserIdIsAndArticleIdIs(user.getId(),articleId);
            article.setCollects(article.getCollects()-1);
            articleRepository.save(article);
            //取消收藏，经验-100
            User user2 = userRepository.findUserById(article.getUserId());
            user2.setGrade(user2.getGrade() - 100);
            userRepository.save(user2);
        }
        session.setAttribute("article",article);

    }
    @RequestMapping("/delete/{id}")
    public void deletecomment(@PathVariable("id")Integer id, HttpSession session){
        Article article = (Article)session.getAttribute("article");
        article.setComments(article.getComments()-1);
        articleRepository.save(article);
        commentRepository.deleteById(id);
        session.setAttribute("article",article);
        System.out.println(id);
        answerRepository.deleteByCommentId(id);
    }

    @JsonFormat
    @RequestMapping("/answer")
    public boolean answer(HttpSession session,@RequestParam("articleId")Integer articleId,@RequestParam("userId")Integer userId,@RequestParam("time")String time
            ,@RequestParam("content")String content,@RequestParam("commentId")int commentId){

        User user = (User)session.getAttribute("user");
        String headpicture = user.getHeadpicture();
        String name = user.getName();
        //回答人的id
        int answerId = user.getId();
        Answer answer = new Answer();
        answer.setAnswerId(answerId);
        answer.setArticleId(articleId);
        answer.setTime(time);
        answer.setUserId(userId);
        answer.setHeadpicture(headpicture);
        answer.setName(name);
        answer.setContent(content);
        answer.setCommentId(commentId);
        answerRepository.save(answer);
        System.out.println(answer.toString());

        return true;
    }

    //视频操作专区
    @RequestMapping("/addmovielike")
    public void addmovielike(@RequestParam("videoId")Integer videoId){
        Video video = videoRepository.findVideoById(videoId);
        video.setVideoLike(video.getVideoLike()+1);
        videoRepository.save(video);
    }

    @RequestMapping("/addmoviedislike")
    public void addmoviedislike(@RequestParam("videoId")Integer videoId){
        Video video = videoRepository.findVideoById(videoId);
        video.setVideoDislike(video.getVideoDislike()+1);
        videoRepository.save(video);
    }

    @RequestMapping("/savevideosay")
    public void savevideosay(@RequestParam("videoId")Integer videoId, @RequestParam("content")String content,
                             @RequestParam("date")String date, @RequestParam("headpicture")String headpicture,
                             @RequestParam("name")String name, Model model, HttpSession session){
        Videocomment videocomment = new Videocomment();
        videocomment.setContent(content);
        videocomment.setDate(date);
        videocomment.setHeadpicture(headpicture);
        videocomment.setVideoId(videoId);
        videocomment.setName(name);
        videocommentRepository.save(videocomment);
    }

    //全景图跳转
    @RequestMapping("fullview")
    public String full(){
        return "fullview";
    }

    @RequestMapping("/baidu")
    @ResponseBody
    public String baidu(HttpSession session, @RequestParam("keyword") String keyword){

//        if(keyword.equals("TLSearch") || keyword.equals("BaiduSearch")){
//            return null;
//        }
        User user = (User)session.getAttribute("user");
        Search search = new Search();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String startTime = sdf.format(date);
        search.setContent(keyword);
        search.setDate(startTime);
        search.setUserId(user.getId());
        searchRepository.save(search);

        String keybaidu = (String)session.getAttribute("keywordbaidu");
        String s = keyword;
//        System.out.println(keybaidu);
        if(keybaidu == null){
//            System.out.println("1:"+keybaidu);
            session.setAttribute("keywordbaidu",keyword);
            try {
                s = HtmlParseUtil.baidu(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s;
        }else if(keybaidu.equals(keyword)){

        }else{
            session.setAttribute("keywordbaidu",keyword);
            try {
                s = HtmlParseUtil.baidu(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s;
        }
        return null;
    }

    @RequestMapping("/tl")
    @ResponseBody
    public String tl(HttpSession session, @RequestParam("keyword") String keyword){
        User user = (User)session.getAttribute("user");
        Search search = new Search();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String startTime = sdf.format(date);
        search.setContent(keyword);
        search.setDate(startTime);
        search.setUserId(user.getId());
        searchRepository.save(search);

        String s = session.getAttribute("keywordtl")+"";
        if(s == null){
            session.setAttribute("keywordtl",keyword);
            List<Article> articles = articleRepository.findByContentContainingOrTitleContaining(keyword, keyword);
            if(articles != null){
                return articles.get(0).getContent();
            }
        }else{
            if(s.equals(keyword)){
                return null;
            }else{
                session.setAttribute("keywordtl",keyword);
                List<Article> articles = articleRepository.findByContentContainingOrTitleContaining(keyword, keyword);
                if(articles != null){
                    return articles.get(0).getContent();
                }
            }

        }

        return "0";
    }





    @RequestMapping("/recomendfriend")
    public ModelAndView recommendfriend(Model model, HttpSession session){
        User user = (User)session.getAttribute("user");
        //随机取两个用户推荐
        List<User>userList = userRepository.findAll();
        double m = userList.size();
        int ok = (int)(Math.random()*(m - 1) + 1);
        User user1 = userList.get(ok);
        User user2 = userList.get(ok-1);
        String content = new String();
        String content1 = new String();
        String content2 = new String();

        //本用户
        List<Search> list = searchRepository.findByUserId(user.getId());
        for (int i = 0; i < list.size(); i++) {
            content += list.get(i).getContent();
        }

        List<Search> list1 = searchRepository.findByUserId(user1.getId());
        for (int i = 0; i < list1.size(); i++) {
            content1 += list1.get(i).getContent();
        }
        List<Search> list2 = searchRepository.findByUserId(user2.getId());
        for (int i = 0; i < list2.size(); i++) {
            content2 += list2.get(i).getContent();
        }
        String tag1[] = new String[3];
        String tag2[] = new String[3];

        for (int i = 0; i < list1.size() && i < 3; i++) {
            tag1[i] = list1.get(i).getContent();
        }
        for (int i = 0; i < list2.size() && i < 3; i++) {
            tag2[i] = list2.get(i).getContent();
        }
        model.addAttribute("tag1",tag1);
        model.addAttribute("tag2",tag2);

        float a1 = SimilarUtil.SorensenDice(content, content1);
        float a2 = SimilarUtil.SorensenDice(content, content2);

        model.addAttribute("similar1",a1 * 100);
        model.addAttribute("similar2",a2 * 100);

        model.addAttribute("user1",user1);
        model.addAttribute("user2",user2);


        return new ModelAndView("recomfriend","ceshiModel",model);
    }

    //词云图谱生成,嵌入在好友推荐页面
    @RequestMapping("/ciyun")
    public ModelAndView ciyun(HttpSession session, Model model) throws IOException {


        List<Article> list = articleRepository.findAll();
        String str = new String();
        for (int i = 0; i < list.size(); i++) {
            str += list.get(i).getTitle();
        }
        str = str.replaceAll("\\<.*?\\>","");
        str = StringEscapeUtils.unescapeHtml(str);

        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        // 设置分词返回数量(频率最高的50个词)
        frequencyAnalyzer.setWordFrequenciesToReturn(180);
        frequencyAnalyzer.setMinWordLength(2);

        //引入中文解析器
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

        //将字符串传唤为inputstream
        InputStream result = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        //指定文本文件路径，生成词频集合
        final List<WordFrequency> wordFrequencyList=frequencyAnalyzer.load(result);
        //设置图片分辨率
        Dimension dimension = new Dimension(1200,1200);
        //此处的设置采用内置常量即可，生成词云对象
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        //设置边界及字体
        wordCloud.setPadding(2);
        Font font = new Font("宋体",Font.BOLD,30);
//        Font font = new Font("STSong-Weight", 2, 20);
        //设置词云显示的三种颜色，越靠前设置表示词频越高的词语的颜色
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.yellow, Color.white, 30, 30));
        wordCloud.setKumoFont(new KumoFont(font));
        //设置背景色
        wordCloud.setBackgroundColor(new Color(1,165,176));
//        wordCloud.setBackgroundColor(new Color(0,0,0));

        //设置背景图片
//        wordCloud.setBackground(new PixelBoundryBackground("/img/blur.jpg"));
        //设置背景图层为圆形
        wordCloud.setBackground(new CircleBackground(300));
        wordCloud.setFontScalar(new SqrtFontScalar(10, 50));
        //生成词云
        wordCloud.build(wordFrequencyList);
        //返回前端
        OutputStream output = new ByteArrayOutputStream();
        wordCloud.writeToStream("png", output);
        byte[] outputByte = ((ByteArrayOutputStream)output).toByteArray();
        model.addAttribute("ciyun", "data:image/png;base64," + org.apache.commons.codec.binary.Base64.encodeBase64String(outputByte));


        return new ModelAndView("ciyuntu","ciyunModel",model);

    }

    @RequestMapping("relici")
    public ModelAndView relici(Model model){
        List<Search> list = searchRepository.findAll();
        //总数
        double sum = list.size();
        Map<String, Integer> map = new HashMap();

        //统计热力词string-次数
        for (int i = 0; i < list.size(); i++) {
            if(map.containsKey(list.get(i).getContent())){
                map.put(list.get(i).getContent(),map.get(list.get(i).getContent())+1);
            }else{
                map.put(list.get(i).getContent(),1);
            }
        }

        List<String> stringList = new ArrayList<>();
        List<Integer> integerList = new ArrayList<>();
        //按次数排序
        ArrayList<Map.Entry<String, Integer>> sortList = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(sortList, (Comparator<Map.Entry<String, Integer>>) (o1, o2) -> (o2.getValue() - o1.getValue()));

        int tag = 0;
        for(Map.Entry<String, Integer> t:sortList){
            if(tag == 9){
                break;
            }
            tag++;
            stringList.add(t.getKey());
            integerList.add(t.getValue());
        }

        model.addAttribute("stringList",stringList);
        model.addAttribute("integer1",integerList.get(0));
        model.addAttribute("integer2",integerList.get(1));
        model.addAttribute("integer3",integerList.get(2));
        model.addAttribute("integer4",integerList.get(3));
        model.addAttribute("integer5",integerList.get(4));
        model.addAttribute("integer6",integerList.get(5));
        model.addAttribute("integer7",integerList.get(6));
        model.addAttribute("integer8",integerList.get(7));
        model.addAttribute("integer9",integerList.get(8));
        double total = integerList.get(0)+integerList.get(1)+integerList.get(2)+integerList.get(3)+
                integerList.get(4)+integerList.get(5)+integerList.get(6)+integerList.get(7)+integerList.get(8);
        model.addAttribute("total",total);

        return new ModelAndView("relici","reliciModel",model);
    }

    @RequestMapping("/bloglike/{id}")
    public ModelAndView bloglike(@PathVariable("id")Integer id,HttpSession session,Model model){
        User user = (User)session.getAttribute("user");
        Map<Float,Integer> map = new HashMap<Float, Integer>();
        String content = new String();
        //本用户
        List<Search> list = searchRepository.findByUserId(user.getId());
        List<Article> articleList1 = new ArrayList<>();//存放六个相似度最高的文章
        for (int i = 0; i < list.size(); i++) {
            content += list.get(i).getContent();
        }
        List<Article>articleList = articleRepository.findAll();
        if(id+5>articleList.size()){
            id=0;
        }
        float b[] = new float[7];//存放六个相似度最高的数组
        float a[] = new float[articleList.size()];
        for(int i = 0;i< articleList.size();i++){
            float a1 = SimilarUtil.SorensenDice(content, articleList.get(i).getTitle()+articleList.get(i).getType()+
                    articleList.get(i).getContent());
            a1 = (int)(a1*1000)/10.0f;
            map.put(a1,i);
            a[i] = a1;
        }
        Arrays.sort(a);
        for(int i = 0;i < 6;++i,++id){
            b[i] = a[articleList.size()-1-id];
            articleList1.add(articleList.get(map.get(a[id])));
        }
        if(id==24){
            id=0;
        }
        model.addAttribute("id",id);
        model.addAttribute("similars",b);
        model.addAttribute("articles",articleList1);
        return new ModelAndView("bloglike","bloglikemodel",model);
    }





}
