package cn.edu.tl.blog.controller;

import cn.edu.tl.blog.entity.Article;
import cn.edu.tl.blog.entity.Chat;
import cn.edu.tl.blog.entity.User;
import cn.edu.tl.blog.repository.ChatRepository;
import cn.edu.tl.blog.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    UserRepository userRepository;


    //显示聊天内容
    @RequestMapping("/toChat/{id}")
    public String toChat(@PathVariable("id")Integer friendId,HttpSession session){
        User user = (User)session.getAttribute("user");
        user.setGrade(user.getGrade() + 50);
        userRepository.save(user);
        session.setAttribute("user", user);
        session.setAttribute("friendId",friendId);
        return "to5";
    }

    @RequestMapping("/toChat/say")
    public ModelAndView chat(Model model, HttpSession session){
        User user = (User)session.getAttribute("user");
        int friendId = (int)session.getAttribute("friendId");
        session.setAttribute("friendId",friendId);
        List<Chat> list = new ArrayList<>();
        list = chatRepository.findAllByUserIdAndFriendId(user.getId(),friendId);
        User friend = (User)userRepository.findUserById(friendId);
        model.addAttribute("list",list);
        model.addAttribute("user",user);
        model.addAttribute("friend",friend);
        //这是最后的一条消息
        if(list.size() != 0) {
            session.setAttribute("lastmessage", list.get(list.size() - 1));
        }else{
            session.setAttribute("lastmessage", null);
        }
        return new ModelAndView("chat","chatModel",model);
    }

    //发留言
    @RequestMapping("/message")
    public void message(@RequestParam("Inputcontent") String Inputcontent,Model model, HttpSession session){
        Chat chat = new Chat();
        System.out.println(Inputcontent);
        User user = (User)session.getAttribute("user");
        int friendId = (int)session.getAttribute("friendId");
        if(Inputcontent != null) {
            chat.setContent(Inputcontent);
            chat.setTime(new Date());
            chat.setUserId(user.getId());
            chat.setFriendId(friendId);
            chatRepository.save(chat);
        }
//        return new ModelAndView("redirect:/chat/toChat/"+friendId);
    }

    //定时收留言
    @RequestMapping("/message2")
    @ResponseBody
    public Chat message2(HttpSession session){
        Chat chat = new Chat();
        User user = (User)session.getAttribute("user");
        int friendId = (int)session.getAttribute("friendId");
        if(session.getAttribute("lastmessage")!=null) {
            chat = (Chat) session.getAttribute("lastmessage");
        }else{
            chat .setId(0);
        }
        //定义新的信息，
        List<Chat> lastchat = new ArrayList<>();
        //这里数据需要反过来，获取对方的！
        lastchat = chatRepository.findNewMessage(friendId,user.getId(),chat.getId());
        if(lastchat.size() != 0) {
            session.setAttribute("lastmessage",lastchat.get(0));
            return lastchat.get(0);
        }else{
            return null;
        }
    }

}
