package com.yong;

import com.alibaba.fastjson.JSON;
import com.yong.pojo.User;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * es 7.1.x 高级客户端测试 API
 */
@SpringBootTest
class YuyongEsApiApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    /**
     * 创建索引  Request
     */
    @Test
    void createIndex() throws IOException {
        // 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("yong_index");
        // 执行请求  IndicesClient
        CreateIndexResponse createIndexResponse = client.indices()
                .create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    // 获取索引,只能判断是否存在
    @Test
    void getIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("yong_index");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    // 测试删除索引
    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("yuyong_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }


    // 测试添加文档
//    @Test
//    void testAddDocument() throws IOException {
//        // 创建对象
//        User user = new User("zhangsan", 1);
//        // 创建请求
//        IndexRequest request = new IndexRequest("yong_index");
//
//        // 规则 put /yong_index/_doc/1
//        request.id("1");
//        request.timeout("2s");
//
//        // 将数据放入请求  json
//        request.source(JSON.toJSONString(user), XContentType.JSON);
//        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
//
//        System.out.println(indexResponse.toString());
//        System.out.println(indexResponse.status());   // 对应命令返回的 created
//    }
//
//
//    // 获取文档 判断是否存在
//    @Test
//    void testIsExists() throws IOException {
//        GetRequest getRequest = new GetRequest("yong_index", "1");
//        // 不获取返回的 _source 的上下文了
//        getRequest.fetchSourceContext(new FetchSourceContext(false));
//        getRequest.storedFields("_none_");
//
//        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
//        System.out.println(exists);
//    }
//
//    // 获取文档的信息
    @Test
    void testDocument() throws IOException {
        long start = System.currentTimeMillis();
        GetRequest getRequest = new GetRequest("alarm_index", "on3Vx3oBmoCAtdTSGYxp");
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        response.getSource();
        response.getField("time");
        System.out.println(response);  // 返回的全部内容
        System.out.println("查询时间= "+ (System.currentTimeMillis() - start));
    }

    @Test
    void testDocumentSelect() throws IOException {
        long start = System.currentTimeMillis();
        //创建搜索对象
        SearchRequest searchRequest = new SearchRequest();
        //构建查询工具
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //添加查询条件，通过QueryBuilders获取各种查询
        searchSourceBuilder.query(QueryBuilders.rangeQuery("time").gte("1627247123741").lte("1628247124741"));
        searchRequest.source(searchSourceBuilder);
        // 第几页
        searchSourceBuilder.from(10);
        // 取消1w条限制
        searchSourceBuilder.trackTotalHits(true);
        // 每页多少条数据
        searchSourceBuilder.size(10);
        // 设置排序规则
        searchSourceBuilder.sort("time", SortOrder.ASC);
        // 设置超时时间为2s
        searchSourceBuilder.timeout(new TimeValue(2000));
        searchRequest.indices("alarm_index");
        //查询
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        //解析
        SearchHits hits = search.getHits();
        SearchHit[] hits1 = hits.getHits();
        int count = 0;
        for (SearchHit hit : hits1) {
            //获取数据
            String sourceAsString = hit.getSourceAsString();
            count++;
            //打印
            System.out.println("结果::"+sourceAsString);
        }
        System.out.println("查询时间= "+ (System.currentTimeMillis() - start) + "count =" + count + "total=" + hits.getTotalHits().value);
    }

    @Test
    public void scrollQueryTest() throws IOException {
        //        1. 创建查询对象
        String index = "alarm_index";
//        String type = "sms-logs-type";
        SearchRequest searchRequest = new SearchRequest(index);//指定索引
//        searchRequest.types(type);//指定类型
        searchRequest.scroll(TimeValue.timeValueMinutes(1l));//指定存在内存的时长为1分钟
//    2. 封装查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.sort("fee", SortOrder.DESC);
        searchSourceBuilder.size(2);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);


        //        3.执行查询
        // client执行
        HttpHost httpHost = new HttpHost("192.168.43.30", 9200);
        RestClientBuilder restClientBuilder = RestClient.builder(httpHost);
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        System.out.println(scrollId);//获取scorllId


//        4.获取数据
        SearchHit[] hits = searchResponse.getHits().getHits();
        for(SearchHit searchHit : hits){
            System.out.println(searchHit);
        }

        //获取全部的下一页, 不过我不知道这种有什么用?????
        while (true){
            //创建SearchScrollRequest对象
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(TimeValue.timeValueMinutes(1l));//设置1分钟
            SearchResponse scroll = restHighLevelClient.scroll(searchScrollRequest, RequestOptions.DEFAULT);
            SearchHit[] hits1 = scroll.getHits().getHits();
            if(hits1 != null && hits1.length > 0){
                System.out.println("------------下一页--------------");
                for(SearchHit searchHit : hits1){
                    System.out.println(searchHit);
                }

            }else {
                System.out.println("------------结束--------------");
                break;
            }
        }

        //删除ScrollId
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        System.out.println("删除scroll"  + clearScrollResponse);
    }

//
//    // 更新文档测试
//    @Test
//    void testUpdateDocument() throws IOException {
//        UpdateRequest updateRequest = new UpdateRequest("yong_index", "1");
//        updateRequest.timeout("1s");
//
//        User user = new User("庾雍说", 23);
//        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
//        UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
//        System.out.println(response.status());
//        System.out.println(response);
//    }

    // 删除文档记录
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("yong_index", "1");
        deleteRequest.timeout("1s");

        DeleteResponse delete = client.delete(deleteRequest, RequestOptions.DEFAULT);

        System.out.println(delete.status());
    }

    // 批量插入
    @Test
    void testBulkRequest() throws IOException, InterruptedException {

        for(int i= 0; i<1000;i++) {
            Thread.sleep(2000);
            batchIn(i);
        }
    }

    private void batchIn(int num) throws InterruptedException, IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("60s");

        ArrayList<User> list = new ArrayList<>();
        for(int i= 0; i<20000;i++) {
            if (i % 2000 ==0) {
                Thread.sleep(5);
            }
            list.add(new User("8127787750964889197"+i*1000*num, System.currentTimeMillis()+i*1000*num,2,1));
        }
        // 批处理请求
        for (int i = 0; i < list.size(); i++) {

            // 批量操作 添加或者删除
            bulkRequest.add(new IndexRequest("alarm_index")
                    //.id((i + 1) + "")
                    .source(JSON.toJSONString(list.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }



    // 查询
    // SearchRequest 搜索请求
    // SearchSourceBuilder 搜索构造
    // HighlightBuilder 构造高亮
    //
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("yong_index");
        // 构建查询构建器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 搜索高亮
        sourceBuilder.highlighter();

        // 查询条件 我们可以使用 QueryBuilders 工具来实现
        // QueryBuilders.termQuery 精确查询
        // QueryBuilders.matchAllQuery() 查询所有
         TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "yuyong1");
         sourceBuilder.query(termQueryBuilder);


         sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


         searchRequest.source(sourceBuilder);

        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(search.getHits());
        System.out.println(JSON.toJSONString(search.getHits()));
        System.out.println("==============================");
        for (SearchHit hit : search.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }

    }
}
