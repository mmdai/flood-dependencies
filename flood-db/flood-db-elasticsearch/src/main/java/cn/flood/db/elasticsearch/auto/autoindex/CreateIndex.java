package cn.flood.db.elasticsearch.auto.autoindex;

import cn.flood.db.elasticsearch.annotation.ESMetaData;
import cn.flood.db.elasticsearch.auto.util.EnableESTools;
import cn.flood.db.elasticsearch.index.ElasticsearchIndex;
import cn.flood.db.elasticsearch.util.MetaData;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;

/**
 * program: esdemo description: 用于扫描ESMetaData注解的类，并自动创建索引mapping 启动时调用，但如果需要让spring知道哪些bean配置了ESMetaData注解，需要ElasticProcessor
 * author: X-Pacific zhang create: 2019-01-30 18:43
 **/
@AutoConfiguration
// 默认是最低优先级,值越小优先级越高
@Order(1)
public class CreateIndex implements ApplicationListener<ContextRefreshedEvent>,
    ApplicationContextAware {

  @Autowired
  ElasticsearchIndex elasticsearchIndex;
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private ApplicationContext applicationContext;

  /**
   * 扫描ESMetaData注解的类，并自动创建索引mapping
   *
   * @param event
   */
  @SuppressWarnings("unchecked")
  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (event.getApplicationContext().getParent() != null) {
//            return;
    }
    Map<String, Object> beansWithAnnotationMap = this.applicationContext
        .getBeansWithAnnotation(ESMetaData.class);
    logger.info("扫描到@ESMetaData注解bean个数：{}", beansWithAnnotationMap.size());
    beansWithAnnotationMap.forEach((beanName, bean) ->
        {
          try {
            MetaData metaData = elasticsearchIndex.getMetaData(bean.getClass());
            if (metaData.isAutoCreateIndex()) {//配置自动创建索引
              if (metaData.isAlias()) {//当配置了别名后自动创建索引功能将失效
                elasticsearchIndex.createAlias(bean.getClass());
              } else if (!elasticsearchIndex.exists(bean.getClass())) {
                elasticsearchIndex.createIndex(bean.getClass());
                if (EnableESTools.isPrintregmsg()) {
                  logger.info(
                      "创建索引成功，索引名称：" + metaData.getIndexname() + "索引类型：" + metaData.getIndextype());
                }
              }
            }
          } catch (Exception e) {
            logger.error("创建索引不成功", e);
          }
        }
    );
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
