package cn.edu.tl.blog.controller;

import cn.edu.tl.blog.entity.Friendship;
import cn.edu.tl.blog.entity.User;
import cn.edu.tl.blog.repository.FriendshipRepository;
import cn.edu.tl.blog.repository.UserRepository;
import cn.edu.tl.blog.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/login")
public class LoginController{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    UserService userService;

    //heapicture
    @RequestMapping("/picture")
    public void upload(HttpServletResponse response, @RequestParam("name") String name) {
            OutputStream os = null;
            FileInputStream fis = null;
            try {
                File ff = new File(System.getProperty("user.dir"));
                String filePath = ff.getParent()+"www/wwwroot/SpringBootProject/headpicture/";
                os = response.getOutputStream();
                fis = new FileInputStream(filePath+name);
                byte[] buffer = new byte[1024];
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

    //cover
    @RequestMapping("/cover")
    public void cover(HttpServletResponse response, @RequestParam("name") String name) {
        OutputStream os = null;
        FileInputStream fis = null;
        try {
            File ff = new File(System.getProperty("user.dir"));
            String filePath = ff.getParent()+"www/wwwroot/SpringBootProject/cover/";
            os = response.getOutputStream();
            fis = new FileInputStream(filePath+name);
            byte[] buffer = new byte[1024];
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


    //登录
    @RequestMapping
    public ModelAndView login(Model model){
        model.addAttribute("error","");
        return new ModelAndView("login","model",model);
    }

    @RequestMapping("/toSignup")
    public ModelAndView toSignup(Model model){
        model.addAttribute("x",0);
        return new ModelAndView("signup","userModel",model);
    }





    //刚进时候
    @RequestMapping("/toIndex")
    public ModelAndView toIndex(Integer user_id, String password, Boolean rememberMe, Model model, HttpSession session){
        if(rememberMe == null){
            rememberMe = false;
        }
        //shiro.获取当前用户
        Subject subject = SecurityUtils.getSubject();
        //因为这里登录有二次加载，所以要判断
        User user = (User)session.getAttribute("user");
        if(user != null){
            return new ModelAndView("to","model",model);
        }
        //封装用户登陆数据
        UsernamePasswordToken token = new UsernamePasswordToken(""+user_id,password, rememberMe);

        try {
            subject.login(token);//执行登陆方法，如果没有异常就说明登陆成功
            user = (User)subject.getPrincipal();
            session.setAttribute("user",user);
            user.setState(1);
            userRepository.save(user);
            return new ModelAndView("to","model",model);
        }catch (UnknownAccountException e){//用户名不存在
            model.addAttribute("error", "用户名不存在！");
            // 学号或密码错误
            return new ModelAndView("login","model",model);
        }catch (IncorrectCredentialsException e){
            model.addAttribute("error", "密码错误！");
            // 学号或密码错误
            return new ModelAndView("login","model",model);
        }

        //原方法
//        User user2 = (User)session.getAttribute("user");
//        if(user2 != null){
//            return new ModelAndView("to","model",model);
//        }
//        User user = userRepository.findByUserIdAndPassword(user_id,password);
//
//        if (user == null) {
//            model.addAttribute("error", "学号或密码错误！");
//            // 学号或密码错误
//            return new ModelAndView("login","model",model);
//        } else {
//            user.setState(1);
//            session.setAttribute("user", user);
//            userRepository.save(user);
//            return new ModelAndView("to","model",model);
//        }
    }
    //退出登录时先remove原来的session对象
    @RequestMapping("/firstIndex")
    public String firstIndex(HttpSession session){
        User user2 = (User)session.getAttribute("user");
        if(user2!=null) {
            user2.setState(0);
            userRepository.save(user2);
            session.removeAttribute("user");
        }
        return "forward:/login";
    }


    @RequestMapping("/go")
    public ModelAndView to(HttpSession session,Model model) {
        User user = (User)session.getAttribute("user");
        int size = 0;
        //查找好友
        if(friendshipRepository.findAllByUserId(user.getId()) != null) {
            List<Friendship> list2 = friendshipRepository.findAllByUserId(user.getId());
            List<User> list = new ArrayList<>();
            for(int i = 0; i < list2.size(); i++){
                list.add(userRepository.findUserById(list2.get(i).getFriendId()));
            }
            model.addAttribute("friendlist", list);
            size = list.size();
        }else{
            model.addAttribute("friendlist",null);
        }
        model.addAttribute("notfriendlist",null);
        model.addAttribute("user",user);
        model.addAttribute("size",size);
        return new ModelAndView("index","userModel",model);
    }



    //注册

    //判断注册是否成功
    @RequestMapping("/signup1")
    public ModelAndView signup1(Model model,Integer userId, String password,String enter,HttpSession session) {

        User user2 = (User)session.getAttribute("user");
        if(user2 != null){
            return new ModelAndView("to","model",model);
        }
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        User user1 = userService.findUser(user.getUserId());
        if(user.getUserId()==null){
            model.addAttribute("x",3);
            return new ModelAndView("signup","userModel",model);
        }
        if(user.getPassword().equals("")||enter.equals("")){
            model.addAttribute("x",4);
            return new ModelAndView("signup","userModel",model);
        }
        if(user1!=null){
            model.addAttribute("x",1);
            return new ModelAndView("signup","userModel",model);
        }
        else{
            if(!user.getPassword().equals(enter)){
                model.addAttribute("x",2);
                return new ModelAndView("signup","userModel",model);
            }
            else{
                user.setHeadpicture("201608311941005977.gif");
                user.setGrade(0);
                user.setState(1);
                user.setGoodtimes(0);
                user.setRoot(0);
                user.setName("请设置");
                user.setSex("男");
                user.setSpeciality("软件工程");
                userService.addUser(user);
                session.setAttribute("user",user);
                return new ModelAndView("redirect:/login/signup1");
            }
        }
    }

    //红色警告
    @RequestMapping("/noPower")
    public String noPower(){
        return "nopower";
    }

}
