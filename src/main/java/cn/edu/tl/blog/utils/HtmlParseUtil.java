package cn.edu.tl.blog.utils;

import cn.edu.tl.blog.entity.Article;
import cn.edu.tl.blog.entity.Hacontent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//爬别人网站数据用的工具，搜索这种，爬虫工具
public class HtmlParseUtil {

    //爬百度搜索结果页面
    public static String baidu(String keyword) throws IOException {
//        String url = "https://so.csdn.net/so/search/all?q="+keyword;
        String url = "https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=65081411_1_oem_dg&wd="+keyword +
                "&fenlei=256&oq="+keyword+"&rsv_pq=a472730400010e77&rsv_t=70f3EZjezPRS0If5M3jc0qOg8nSdQCZ%2BdxSvvcMXGSffyOgV7mFiFwDC%2FgBe6dBHkKhPMSCo5O0&" +
                "rqlang=cn&rsv_enter=1&rsv_dl=tb&rsv_btype=t&inputT=3663&rsv_sug3=25&rsv_sug1=33&rsv_sug7=101&rsv_sug2=0&rsv_sug4=4675";
//        String url = "https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd="+keyword+"&fenlei=256&rsv_pq=df4b1d180003386e&" +
//        "rsv_t=859bmrPQY0BJ%2FPDl6T94OhXl%2FLS5GCIHwYvkb6T5a7SlboLta1sYZNN6Y5o&" +
//        "rqlang=cn&rsv_dl=tb&rsv_enter=1&rsv_sug3=7&rsv_sug1=5&rsv_sug7=101&rsv_sug2=0&rsv_btype=i&inputT=721&rsv_sug4=2480";
//        String url = "https://baidu.com/s?from=1012852q&word="+keyword;
        //解析网页。(Jsoup返回Document就是浏览器Document对象)
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有你在js中可以使用的方法，这里都能用！
        //获取所有类为c-abstract标签
        Elements elements = document.getElementsByClass("c-row c-gap-top-small");//c-abstract
//        System.out.println(elements.get(0));
        String content = null;
        if(elements.size()==0){
            return "0";
        }

        content = elements.get(0).html();
//        String img = el.getElementsByTag("img").eq(0).attr("src");
        return content;
    }

    //直接爬虫，返回list<Hacontent>数组
    public static List<Hacontent> parseJD(String keywords) throws Exception {
        //获取请求  https://search.jd.com/Search?keyword=java
        //前提，需要联网
        String url = "https://tieba.baidu.com/f/search/res?ie=utf-8&kw=湖北经济学院&qw="+keywords;
        String url2 = "https://tieba.baidu.com/f/search/res?ie=utf-8&kw=湖北经济学院&qw="+keywords+"&rn=10&un=&only_thread=0&sm=1&sd=&ed=&pn=2";
        String url3 = "https://tieba.baidu.com/f/search/res?ie=utf-8&kw=湖北经济学院&qw="+keywords+"&rn=10&un=&only_thread=0&sm=1&sd=&ed=&pn=3";

        //https://tieba.baidu.com/f/search/res?ie=utf-8&kw=%E6%B9%96%E5%8C%97%E7%BB%8F%E6%B5%8E%E5%AD%A6%E9%99%A2&qw=%E7%8E%8B%E8%80%85
        //解析网页。(Jsoup返回Document就是浏览器Document对象)
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有你在js中可以使用的方法，这里都能用！
        //获取所有类为s_post标签
        Elements elements = document.getElementsByClass("s_post");
        // 获取所有的div元素
//        Elements div = elements.getElementsByTag("div");
        //获取元素中的内容,这里的el就是 每一个li标签了
        ArrayList<Hacontent> goodsList = new ArrayList<>();

        for (Element el : elements) {

            if(!el.getElementsByClass("p_content").eq(0).text().equals("")) {
                String img = el.getElementsByTag("img").eq(0).attr("src");
                String title = el.getElementsByClass("p_title").eq(0).text();
                String href = el.getElementsByTag("a").eq(0).attr("href");
                String content = el.getElementsByClass("p_content").eq(0).text();
                String date = el.getElementsByClass("p_green p_date").eq(0).text();
                String username = el.getElementsByClass("p_violet").eq(1).text();


                Hacontent hacontent = new Hacontent();
                hacontent.setTitle(title);
                if (img.equals("")) {
                    hacontent.setImg("/images/12.jpg");
                } else {
                    hacontent.setImg(img);
                }
                hacontent.setHref("https://tieba.baidu.com" + href);
                hacontent.setDate(date);
                hacontent.setContent(content);
                hacontent.setUsername(username);
                goodsList.add(hacontent);
            }
        }

        //解析网页。(Jsoup返回Document就是浏览器Document对象)
        Document document2 = Jsoup.parse(new URL(url2), 30000);
        //所有你在js中可以使用的方法，这里都能用！
        //获取所有类为s_post标签
        Elements elements2 = document2.getElementsByClass("s_post");
        //获取元素中的内容,这里的el就是 每一个li标签了

        for (Element el : elements2) {
            if (!el.getElementsByClass("p_content").eq(0).text().equals("")) {
                String img = el.getElementsByTag("img").eq(0).attr("src");
                String title = el.getElementsByClass("p_title").eq(0).text();
                String href = el.getElementsByTag("a").eq(0).attr("href");
                String content = el.getElementsByClass("p_content").eq(0).text();
                String date = el.getElementsByClass("p_green p_date").eq(0).text();
                String username = el.getElementsByClass("p_violet").eq(1).text();



                Hacontent hacontent = new Hacontent();
                hacontent.setTitle(title);
                if (img.equals("")) {
                    hacontent.setImg("/images/12.jpg");
                } else {
                    hacontent.setImg(img);
                }
                hacontent.setHref("https://tieba.baidu.com" + href);
                hacontent.setDate(date);
                hacontent.setContent(content);
                hacontent.setUsername(username);
                goodsList.add(hacontent);
            }
        }

        //解析网页。(Jsoup返回Document就是浏览器Document对象)
        Document document3 = Jsoup.parse(new URL(url3), 30000);
        //所有你在js中可以使用的方法，这里都能用！
        //获取所有类为s_post标签
        Elements elements3 = document3.getElementsByClass("s_post");

//        Elements div = elements.getElementsByTag("div");
        //获取元素中的内容,这里的el就是 每一个li标签了

        for (Element el : elements3) {
            if (!el.getElementsByClass("p_content").eq(0).text().equals("")) {
                String img = el.getElementsByTag("img").eq(0).attr("src");
                String title = el.getElementsByClass("p_title").eq(0).text();
                String href = el.getElementsByTag("a").eq(0).attr("href");
                String content = el.getElementsByClass("p_content").eq(0).text();
                String date = el.getElementsByClass("p_green p_date").eq(0).text();
                String username = el.getElementsByClass("p_violet").eq(1).text();


                Hacontent hacontent = new Hacontent();
                hacontent.setTitle(title);
                if (img.equals("")) {
                    hacontent.setImg("/images/12.jpg");
                } else {
                    hacontent.setImg(img);
                }
                hacontent.setHref("https://tieba.baidu.com" + href);
                hacontent.setDate(date);
                hacontent.setContent(content);
                hacontent.setUsername(username);
                goodsList.add(hacontent);
            }
        }
        return goodsList;
    }
}