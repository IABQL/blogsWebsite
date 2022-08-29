package com.blogswebsite.service;

import com.alibaba.fastjson.JSON;
import com.blogswebsite.entity.Blog;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ElasticsearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //插入数据
    public void addDoc(Blog blog) {

        try{
            IndexRequest indexRequest = new IndexRequest("blog")
                    .id(blog.getId()+"").source(JSON.toJSONString(blog), XContentType.JSON);

            restHighLevelClient.index(indexRequest,RequestOptions.DEFAULT);
        }catch (IOException e){

        }
    }


    //删除数据
    public void delDoc(Integer id) {
        try{
            DeleteRequest deleteRequest = new DeleteRequest("blog",id+"");

            restHighLevelClient.delete(deleteRequest,RequestOptions.DEFAULT);
        }catch (IOException e){

        }
    }



    //高亮搜索
    public Map<String,Object> highLightSearch(String searchKey,Integer currentPage) throws IOException {

        searchKey = searchKey.trim();

        Map<String,Object> res = new HashMap<>();

        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("blog");
        //搜索构建对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();



        //多字段查询,查询title、blogExplain字段中存在 searchKey 的数据，取交集
        QueryStringQueryBuilder query = QueryBuilders.queryStringQuery(searchKey)
                .field("title").field("blogExplain").defaultOperator(Operator.AND);

        sourceBuilder.query(query);//传递查询


        //设置高亮
        HighlightBuilder highlight = new HighlightBuilder();
        //高亮字段
        highlight.field("title").field("blogExplain");
        //前缀
        highlight.preTags("<em style='color:red'>");
        //后缀
        highlight.postTags("</em>");

        sourceBuilder.highlighter(highlight);//传递高亮


        //查询构建对象传给搜索请求对象
        searchRequest.source(sourceBuilder);

        //分页
        sourceBuilder.from(currentPage);
        sourceBuilder.size(7);

        //提交请求
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = response.getHits();
        //获取查询结果总数
        long total = hits.getTotalHits().value;

        res.put("total",total);


        //获取查询数据
        List<Blog> list = new ArrayList<>();

        //查询到的数据集合
        SearchHit[] hits1 = hits.getHits();

        //遍历数组，读出数据，替换高亮
        for (SearchHit hit : hits1) {
            //hit中的"_source"字段为存入的数据
            String sourceAsString = hit.getSourceAsString();
            //转为BlogWebsite对象
            Blog blogWebsite = JSON.parseObject(sourceAsString, Blog.class);

            //获取高亮结果（字段"title"）(字段"blogExplain")，替换BlogWebsite对象中的"title","blogExplain"
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            HighlightField high_title = highlightFields.get("title");//获取高亮结果（字段"title"）
            HighlightField high_blogExplain = highlightFields.get("blogExplain");//获取高亮结果（字段"blogExplain"）

            Text[] title = null;
            Text[] blogExplain = null;
            if (high_title != null){
                title = high_title.fragments();
            }
            if (high_blogExplain != null){
                blogExplain = high_blogExplain.fragments();
            }

            //替换字段，"title""blogExplain"
            if (title != null){
                blogWebsite.setTitle(title[0].toString());
            }
            if (blogExplain != null){
                blogWebsite.setBlogExplain(blogExplain[0].toString());
            }

            list.add(blogWebsite);
        }
        res.put("blogList",list);
        return res;
    }
}
