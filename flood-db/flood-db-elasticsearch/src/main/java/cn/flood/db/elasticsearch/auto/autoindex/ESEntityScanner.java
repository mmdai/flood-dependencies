package cn.flood.db.elasticsearch.auto.autoindex;

import cn.flood.db.elasticsearch.annotation.ESMetaData;
import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * program: esdemo description: 扫描ESMetaData的注解的bean（就是es数据结构的实体类）给spring管理 author: X-Pacific zhang
 * create: 2019-01-24 15:24
 **/
public class ESEntityScanner extends ClassPathBeanDefinitionScanner {

  public ESEntityScanner(BeanDefinitionRegistry registry) {
    super(registry);
  }

  @Override
  public void registerDefaultFilters() {
    this.addIncludeFilter(new AnnotationTypeFilter(ESMetaData.class));
  }

  @Override
  public Set<BeanDefinitionHolder> doScan(String... basePackages) {
    Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
    return beanDefinitions;
  }

  @Override
  public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    return super.isCandidateComponent(beanDefinition) && beanDefinition.getMetadata()
        .hasAnnotation(ESMetaData.class.getName());
  }
}
