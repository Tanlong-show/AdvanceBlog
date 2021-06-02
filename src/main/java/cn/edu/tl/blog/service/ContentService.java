package cn.edu.tl.blog.service;

import cn.edu.tl.blog.entity.Article;
import cn.edu.tl.blog.entity.Hacontent;
import cn.edu.tl.blog.repository.ArticleRepository;
import cn.edu.tl.blog.utils.HtmlParseUtil;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// 业务编写
//后期改成搜索所有的关键词
@Service
public class ContentService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    //索引名字
    public static String name;

    //1、解析数据放入 es 索引中
    public boolean parseContent(String keyword, String name) throws Exception {
        ContentService.name = name;
        //1.创建索引的请求
        GetIndexRequest request = new GetIndexRequest(name);
        //2客户端执行请求，请求后获得响应
        boolean exist =  client.indices().exists(request, RequestOptions.DEFAULT);
        //如果查询过就不会再爬虫存储
        if(exist){
            return true;
        }

        //运用爬虫工具包
        List<Hacontent> contents = HtmlParseUtil.parseJD(keyword);
        // 把查询到的数据放入es 中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i = 0; i < contents.size(); i++) {
//            System.out.println(JSON.toJSONString(contents.get(i)));
            bulkRequest.add(
                    new IndexRequest(name)
                            .source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    //2、获取这些数据实现搜索功能
    public ArrayList<Map<String, Object>> searchPage(String keyword) throws IOException {

        // 条件搜索
        SearchRequest searchRequest = new SearchRequest(name);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //精准匹配
//        TermQueryBuilder title = QueryBuilders.termQuery("content", keyword);
        MatchPhraseQueryBuilder title = QueryBuilders.matchPhraseQuery("title", keyword);
        sourceBuilder.query(title);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields: search.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        System.out.println(list.size());
        return list;
    }

    //3、获取这些数据实现高亮功能
    public List<Map<String,Object>> searchPageHighlightBuilder(String keyword,int pageNo,int pageSize) throws IOException {
        if(pageNo<=1){
            pageNo = 1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest(name);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        //精准匹配
        MatchPhraseQueryBuilder title = QueryBuilders.matchPhraseQuery("title", keyword);
        sourceBuilder.query(title);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false); //多个高亮显示！
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit hit: search.getHits().getHits()) {
            //解析高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title1 = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //解析高亮的字段
            if(title1 != null){
                Text[] fragments = title1.fragments();
                String n_title = "";
                for (Text text : fragments) {
                    n_title += text;
                }
                sourceAsMap.put("title",n_title);
            }
            list.add(sourceAsMap);
        }
        return list;
    }
}