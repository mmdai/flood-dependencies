package cn.flood.canal.configuration.autoconfigure;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import cn.flood.canal.handler.CanalThreadUncaughtExceptionHandler;
import cn.flood.canal.configuration.properties.CanalProperties;

import java.util.concurrent.*;

/**
 * @author gujiachun
 */
@AutoConfiguration
public class ThreadPoolAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "true",matchIfMissing = true)
    public ExecutorService executorService() {
        BasicThreadFactory factory = new BasicThreadFactory.Builder().namingPattern("canal-execute-thread-%d")
                .uncaughtExceptionHandler(new CanalThreadUncaughtExceptionHandler()).build();
//        return Executors.newFixedThreadPool(10, factory);
        return new ThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), factory, new ThreadPoolExecutor.AbortPolicy());
    }
}
