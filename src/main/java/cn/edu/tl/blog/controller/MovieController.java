package cn.edu.tl.blog.controller;

import cn.edu.tl.blog.entity.*;
import cn.edu.tl.blog.repository.SearchRepository;
import cn.edu.tl.blog.repository.VideoRepository;
import cn.edu.tl.blog.repository.VideocommentRepository;
import cn.edu.tl.blog.utils.HtmlParseUtil;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private VideocommentRepository videocommentRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private SearchRepository searchRepository;

    @RequestMapping("/video/{id}")
    public ModelAndView upload(HttpSession session, Model model, @PathVariable("id") Integer id) {
        int page = id+17;//page值，视频对应
        Video video = videoRepository.getOne(id);
        List<Videocomment> videocomment = videocommentRepository.findAllByVideoId(id);
        model.addAttribute("percent",String.format("%.2f", (double)video.getVideoLike()/(video.getVideoDislike()+video.getVideoLike())*100));
        model.addAttribute("video",video);
        model.addAttribute("comments",videocomment);
        User user = (User)session.getAttribute("user");
        model.addAttribute("user",user);
        String pagestr="//player.bilibili.com/player.html?aid=68373450&bvid=BV12J41137hu&cid=118499718&page="+page+"&as_wide=1&high_quality=1&danmaku=0&t=1";
        model.addAttribute("pagestr",pagestr);
        return new ModelAndView("movie-single","videoModel",model);
    }


    //vedio
    @RequestMapping("/showvideo")
    public void upload(HttpServletResponse response, @RequestParam("name") String name) {
        OutputStream os = null;
        FileInputStream fis = null;
        try {
            File ff = new File(System.getProperty("user.dir"));
            String filePath = ff.getParent()+"www/wwwroot/SpringBootProject/video/";
            os = response.getOutputStream();
            fis = new FileInputStream(filePath+name);
            int i=fis.available(); //得到文件大小
            byte[] buffer = new byte[i];
            while(fis.read(buffer) != -1) {
                os.write(buffer);
            }
        }catch (Exception e){

        }
        finally {
            try {
                os.flush();
                os.close();
                fis.close();
            }catch (Exception e){

            }
        }
    }

    //查电影
    @RequestMapping("/findmovie")
    public ModelAndView findmovie(HttpSession session, Model model,@RequestParam("keyword") String keyword) {
        User user = (User)session.getAttribute("user");
        Search search = new Search();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String startTime = sdf.format(date);
        search.setContent(keyword);
        search.setDate(startTime);
        search.setUserId(user.getId());
        searchRepository.save(search);

        model.addAttribute("movies",videoRepository.findByTitleContainingOrUserNameContainingOrSummarizeContainingOrTypeContaining(keyword,keyword,keyword,keyword));
        return new ModelAndView("tv-shows","moviesModel",model);
    }

    //查舆论贴吧
    @RequestMapping("/findmkey")
    public ModelAndView a(HttpSession session, Model model, @RequestParam("keyword")String keyword) throws Exception {
        User user = (User)session.getAttribute("user");
        Search search = new Search();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String startTime = sdf.format(date);
        search.setContent(keyword);
        search.setDate(startTime);
        search.setUserId(user.getId());
        searchRepository.save(search);


        model.addAttribute("keyword",keyword);

            List<Hacontent> hacontentList = new ArrayList<>();
            hacontentList = HtmlParseUtil.parseJD(keyword);
            model.addAttribute("size",hacontentList.size());
            model.addAttribute("hacontentList",hacontentList);

//            for(int i = 0; i < hacontentList.size(); i++ ){
//            System.out.println("tltlt: "+hacontentList.get(i).getTitle());
//        }

        //删除，减内存，下次更新
//        DeleteIndexRequest request = new DeleteIndexRequest("jd_goods");
//        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);

        return new ModelAndView("luntan2","forummodel",model);
    }

}
