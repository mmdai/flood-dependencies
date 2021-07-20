/**  
* <p>Title: DataSourceAnnotationAspect.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月12日  
* @version 1.0  
*/  
package cn.flood.datasource.aop.impl;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.flood.datasource.aop.DataSourceAnnotation;
import cn.flood.datasource.dynamic.DataSourceHolder;
import cn.flood.datasource.enums.DataSourceEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 
* <p>Title: DataSourceAnnotationAspect</p>  
* <p>Description: 切换数据源Advice</p>  
* @author mmdai  
* @date 2019年11月11日
 */
@Aspect
@Order(-10) // 保证该AOP在@Transactional之前执行
@Component
@Slf4j
public class DataSourceAnnotationAspect {
	
	@Before("@annotation(ds)")
    public void changeDataSource(JoinPoint point, DataSourceAnnotation ds) throws Throwable {
        String dsId = ds.name();
        try {
            DataSourceEnum dataSourceKey = DataSourceEnum.valueOf(dsId);
            DataSourceHolder.setDataSourceKey(dataSourceKey);
        } catch (Exception e) {
            log.error("数据源[{}]不存在，使用默认数据源 > {}", ds.name(), point.getSignature());
        }
    }

    @After("@annotation(ds)")
    public void restoreDataSource(JoinPoint point, DataSourceAnnotation ds) {
        log.debug("Revert DataSource : {transIdo} > {}", ds.name(), point.getSignature());
        DataSourceHolder.clearDataSourceKey();
    }

}
