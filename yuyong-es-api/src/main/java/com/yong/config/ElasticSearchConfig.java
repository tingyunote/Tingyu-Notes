package com.yong.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// spring 两个步骤
// 1、找对象
// 2、放入spring中待使用
// 3、如果是SpringBoot就先分析源码
// xxxx AutoConfiguration   xxxProperties
@Configuration
public class ElasticSearchConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("47.104.xx.105",9200,"http")));
    }
}
