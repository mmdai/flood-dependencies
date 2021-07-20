package cn.flood.threadpool.endpoint;

import cn.flood.json.JsonUtils;
import cn.flood.threadpool.DynamicThreadPoolManager;
import cn.flood.threadpool.config.DynamicThreadPoolProperties;
import cn.flood.threadpool.FloodThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池信息端点
 */
@Endpoint(id = "thread-pool")
public class ThreadPoolEndpoint {

    @Autowired
    private DynamicThreadPoolManager dynamicThreadPoolManager;

    @Autowired
    private DynamicThreadPoolProperties dynamicThreadPoolProperties;

    @ReadOperation
    public Map<String, Object> threadPools() {
        Map<String, Object> data = new HashMap<>();

        List<Map> threadPools = new ArrayList<>();
        dynamicThreadPoolProperties.getExecutors().forEach(prop -> {
            FloodThreadPoolExecutor executor = dynamicThreadPoolManager.getThreadPoolExecutor(prop.getThreadPoolName());
            AtomicLong rejectCount = dynamicThreadPoolManager.getRejectCount(prop.getThreadPoolName());

            Map<String, Object> pool = new HashMap<>();

            Map config = JsonUtils.toJavaObject(JsonUtils.toJSONString(prop), Map.class);
            pool.putAll(config);
            pool.put("activeCount", executor.getActiveCount());
            pool.put("completedTaskCount", executor.getCompletedTaskCount());
            pool.put("largestPoolSize", executor.getLargestPoolSize());
            pool.put("taskCount", executor.getTaskCount());
            pool.put("rejectCount", rejectCount == null ? 0 : rejectCount.get());
            pool.put("waitTaskCount", executor.getQueue().size());
            threadPools.add(pool);
        });

        data.put("threadPools", threadPools);
        return data;
    }

}
