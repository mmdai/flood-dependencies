/**  
* <p>Title: TransactionConfiguration.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年3月22日  
* @version 1.0  
*/  
package cn.flood.config;

import java.util.Properties;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.codingapi.txlcn.common.util.Transactions;
import com.codingapi.txlcn.tc.aspect.interceptor.TxLcnInterceptor;
import com.codingapi.txlcn.tc.aspect.weave.DTXLogicWeaver;

/**  
* <p>Title: AopTypeDTXConfiguration</p>
* <p>Description: 通过AOP配置本地事务与分布式事务,service b, service c 用的注解方式，注意区分，非必须如此配置，可以注解，也可以声明式</p>
* @author mmdai  
* @date 2019年3月22日  
*/
@Configuration
@EnableTransactionManagement
public class AopTypeDTXConfiguration {
	
	/**
     * 本地事务配置
     * @param transactionManager
     * @return
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public TransactionInterceptor transactionInterceptor(PlatformTransactionManager transactionManager) {
        Properties properties = new Properties();
        properties.setProperty("*", "PROPAGATION_REQUIRED,-Throwable");
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        transactionInterceptor.setTransactionManager(transactionManager);
        transactionInterceptor.setTransactionAttributes(properties);
        return transactionInterceptor;
    }

    /**
     * 分布式事务配置 设置为LCN模式
     * @param dtxLogicWeaver
     * @return
     */
    @ConditionalOnBean(DTXLogicWeaver.class)
    @Bean
    public TxLcnInterceptor txLcnInterceptor(DTXLogicWeaver dtxLogicWeaver) {
        TxLcnInterceptor txLcnInterceptor = new TxLcnInterceptor(dtxLogicWeaver);
        Properties properties = new Properties();
        properties.setProperty(Transactions.DTX_TYPE, Transactions.LCN);
        properties.setProperty(Transactions.DTX_PROPAGATION, "REQUIRED");
        txLcnInterceptor.setTransactionAttributes(properties);
        return txLcnInterceptor;
    }

    @Bean
    @Primary
    public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
        //需要调整优先级，分布式事务在前，本地事务在后。
        beanNameAutoProxyCreator.setInterceptorNames("txLcnInterceptor","transactionInterceptor");
        beanNameAutoProxyCreator.setBeanNames("*Impl");
        return beanNameAutoProxyCreator;
    }

}
