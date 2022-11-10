package cn.flood.db.elasticsearch;

import cn.flood.db.elasticsearch.properties.ElasticsearchProperties;
import cn.flood.db.elasticsearch.properties.RestClientPoolProperties;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;


/**
 * ES 自动配置
 *
 */
@EnableConfigurationProperties({ElasticsearchProperties.class, RestClientPoolProperties.class})
@ComponentScan("cn.flood.elasticsearch")
public class ElasticSearchClientAutoConfiguration {

    @Bean(destroyMethod = "close")//这个close是调用RestHighLevelClient中的close
    @Scope("singleton")
    public RestHighLevelClient restHighLevelClient(ElasticsearchProperties elasticsearchProperties,
                                                   RestClientPoolProperties poolProperties) {
        String[] hostNames = elasticsearchProperties.getHostNames().split(",");
        HttpHost[] httpHostList = new HttpHost[hostNames.length];
        for(int i=0; i<hostNames.length; i++){
            String[] host = hostNames[i].split(":");
            httpHostList[i] = new HttpHost(host[0].replaceAll("\\s*", ""), Integer.parseInt(host[1].replaceAll("\\s*", "")));
        }
        RestClientBuilder restClientBuilder = RestClient.builder(
                httpHostList
        );
        /**
         * 异步httpclient连接延时配置
         */
        Long strategy = poolProperties.getKeepAliveStrategy();
        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(poolProperties.getConnectTimeOut())
                    .setSocketTimeout(poolProperties.getSocketTimeOut())
                    .setConnectionRequestTimeout(poolProperties.getConnectionRequestTimeOut());
            return requestConfigBuilder;
        });

        /**
         * 异步httpclient连接数配置
         */
        if (StringUtils.hasText(elasticsearchProperties.getUsername()) && StringUtils.hasText(elasticsearchProperties.getPassword())) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword()));
            restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder.disableAuthCaching();
                httpClientBuilder.setMaxConnTotal(poolProperties.getMaxConnectNum());
                httpClientBuilder.setMaxConnPerRoute(poolProperties.getMaxConnectPerRoute());
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                if (strategy > 0){
                    httpClientBuilder.setKeepAliveStrategy((httpResponse, httpContext) -> strategy);
                }
                return httpClientBuilder;
            });
        }else{
            restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder.disableAuthCaching();
                httpClientBuilder.setMaxConnTotal(poolProperties.getMaxConnectNum());
                httpClientBuilder.setMaxConnPerRoute(poolProperties.getMaxConnectPerRoute());
                if (strategy > 0){
                    httpClientBuilder.setKeepAliveStrategy((httpResponse, httpContext) -> strategy);
                }
                return httpClientBuilder;
            });
        }
        return new RestHighLevelClient(restClientBuilder);
    }


}