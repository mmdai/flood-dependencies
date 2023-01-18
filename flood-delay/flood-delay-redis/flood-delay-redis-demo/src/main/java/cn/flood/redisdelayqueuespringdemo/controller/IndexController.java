package cn.flood.redisdelayqueuespringdemo.controller;

import cn.flood.db.redis.service.RedisService;
import cn.flood.delay.core.RedisDelayQueueContext;
import cn.flood.delay.entity.DelayQueueJob;
import cn.flood.delay.service.RedisDelayQueue;
import cn.flood.redisdelayqueuespringdemo.bo.User;
import cn.flood.redisdelayqueuespringdemo.delayqueues.DelayQueueDemo2;
import cn.flood.redisdelayqueuespringdemo.delayqueues.TopicEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Clock;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Date 2019/8/1 9:40 AM
 **/
@Controller
@ResponseBody
public class IndexController {


    @Autowired
    RedisDelayQueue redisDelayQueue;


    @Autowired
    RedisDelayQueueContext redisDelayQueueContext;

    @Autowired
    DelayQueueDemo2 delayQueueDemo2;

    @Autowired
    RedisService redisService;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     *
     */
    @GetMapping("/addJob")
    public void addJob(Long rt,Integer type ){
        if(rt ==null){
            rt = Clock.systemDefaultZone().millis() + 300000;
        }
        DelayQueueJob myArgs = new DelayQueueJob();
        String id = UUID.randomUUID().toString();
        myArgs.setId(id);
        myArgs.setBody("lalalalala");
        redisDelayQueue.add(myArgs,TopicEnums.DEMO_TOPIC.getTopic(),rt);
    }

    @GetMapping("/addJob3")
    public void addJob3(){
        delayQueueDemo2.addDemo2DelayQueue(UUID.randomUUID().toString(),300000);
    }


    @GetMapping("/addJob2")
    public void addJob2(Long delayTime,String userId ){

        delayQueueDemo2.addDemo2DelayQueue(userId,300000);
    }

    @GetMapping("/delJob2")
    public void delJob2(Long delayTime,String userId ){
        delayQueueDemo2.delDemo2Queue(userId);
    }

    @GetMapping("/save")
    public void save(){
        User user = new User();
        user.setId("1234");
        user.setDate(new Date());
        user.setName("哈哈");
        redisService.set("test", user, 3L, TimeUnit.MINUTES);
    }
    @GetMapping("/get")
    public void get(){
        User user = redisService.get("test");
        System.out.println(user.getId());
    }

    @GetMapping("/save1")
    public void save1(){
        redisService.addZSet("test1", "22", "44","33","22");
    }
    @GetMapping("/get1")
    public void get1(){
        Map<Double, Object> value = redisService.popMaxByScoreZSet("test1", 2);
        System.out.println(value);
    }
    @GetMapping("/save2")
    public void save2(){
        redisService.lpushAll("test2", "22", "44","33", "22");
    }

    @GetMapping("/get2")
    public void get2(){
        String value = redisService.lpop("test2");
        System.out.println(value);
    }

    @GetMapping("/save3")
    public void save3(){
        Object object = redisTemplate.opsForList().leftPop("mmdai", 30, TimeUnit.SECONDS);
    }



    private Date getDate(long millis){
        Date date = new Date();
        date.setTime(millis);
        return date;
    }



}
