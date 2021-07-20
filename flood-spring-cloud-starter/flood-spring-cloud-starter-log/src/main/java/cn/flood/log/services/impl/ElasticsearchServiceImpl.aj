package cn.flood.log.services.impl;

import cn.flood.log.model.Audit;
import cn.flood.log.services.IAuditService;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 */
@Slf4j
//@ConditionalOnProperty(name = "spring.audit-log.log-type", havingValue = "es")
//@ConditionalOnClass(RestHighLevelClient.class)
public class ElasticsearchServiceImpl implements IAuditService {

//    private final ElasticsearchRestTemplate searchRestTemplate;
//
//    public ElasticsearchServiceImpl(@Autowired(required = false) LogElasticsearchProperties elasticsearchProperties,
//                                    @Autowired(required = false) LogRestClientPoolProperties poolProperties,
//                                    RestHighLevelClient restHighLevelClient){
//        if(elasticsearchProperties != null &&  StringUtils.isNotEmpty(elasticsearchProperties.getHostNames())){
//            String[] hostNames = elasticsearchProperties.getHostNames().split(",");
//            HttpHost[] httpHostList = new HttpHost[hostNames.length];
//            for(int i=0; i<hostNames.length; i++){
//                String[] host = hostNames[i].split(":");
//                httpHostList[i] = new HttpHost(host[0].replaceAll("\\s*", ""), Integer.parseInt(host[1].replaceAll("\\s*", "")));
//            }
//            RestClientBuilder restClientBuilder = RestClient.builder(
//                    httpHostList
//            );
//            /**
//             * 异步httpclient连接延时配置
//             */
//            restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> {
//                requestConfigBuilder.setConnectTimeout(poolProperties.getConnectTimeOut())
//                        .setSocketTimeout(poolProperties.getSocketTimeOut())
//                        .setConnectionRequestTimeout(poolProperties.getConnectionRequestTimeOut());
//                return requestConfigBuilder;
//            });
//            /**
//             * 异步httpclient连接数配置
//             */
//            if (org.springframework.util.StringUtils.hasText(elasticsearchProperties.getUsername()) && org.springframework.util.StringUtils.hasText(elasticsearchProperties.getPassword())) {
//                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//                credentialsProvider.setCredentials(AuthScope.ANY,
//                        new UsernamePasswordCredentials(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword()));
//                restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder ->
//                        httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider).
//                                setMaxConnTotal(poolProperties.getMaxConnectNum()).
//                                setMaxConnPerRoute(poolProperties.getMaxConnectPerRoute())
//                );
//            }
//            restHighLevelClient = new RestHighLevelClient(restClientBuilder);
//        }
//        this.searchRestTemplate = new ElasticsearchRestTemplate(restHighLevelClient);
//    }
//
//    @Async
    @Override
    public void save(Audit audit) {
        //es插入
//        restHighLevelClient.
    }
//
//    @PostConstruct
    public void init() {
        //初始化es表结果
    }
}
