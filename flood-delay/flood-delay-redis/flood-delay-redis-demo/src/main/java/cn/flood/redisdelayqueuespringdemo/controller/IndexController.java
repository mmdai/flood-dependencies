package cn.flood.redisdelayqueuespringdemo.controller;

import cn.flood.base.core.json.JsonUtils;
import cn.flood.db.redis.service.RedisService;
import cn.flood.delay.redis.RDQueueTemplate;
import cn.flood.delay.redis.core.Message;
import cn.flood.delay.redis.exception.RDQException;
import cn.flood.redisdelayqueuespringdemo.bo.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Date 2019/8/1 9:40 AM
 **/
@RestController
public class IndexController {

  @Autowired
  RedisService redisService;
  @Autowired
  RedisTemplate redisTemplate;

  @Autowired
  private RDQueueTemplate rdQueueTemplate;

  /**
   *
   */
  @GetMapping("/push")
  public String addJob() throws RDQException {
    Message<User> message = new Message<>();
    message.setTopic("order-cancel");
    User user = new User();
    user.setId(1234);
//        user.setDateTime(LocalDateTime.now());
    user.setName("哈哈");
    user.setContent("我猜你是ABC");
    message.setMsg(user);
    message.setMsgId(UUID.randomUUID().toString().replaceAll("-", ""));
    message.setDelayTime(60);
    rdQueueTemplate.asyncPush(message, (s, throwable) -> {
      if (null != throwable) {
        throwable.printStackTrace();
      } else {
        System.out.println("s" + s);
      }
    });
    return "推送成功";
  }

  /**
   *
   */
  @GetMapping("/push1")
  public String addJob1() throws RDQException {
    Message<User> message = new Message<>();
    message.setTopic("order-cancel1");
    User user = new User();
    user.setId(1234);
//        user.setDateTime(LocalDateTime.now());
    user.setName("哈哈");
    user.setContent("我猜你是CCCCCCCCCC");
    message.setMsg(user);
    message.setMsgId(UUID.randomUUID().toString().replaceAll("-", ""));
    message.setDelayTime(60);
    rdQueueTemplate.asyncPush(message, (s, throwable) -> {
      if (null != throwable) {
        throwable.printStackTrace();
      } else {
        System.out.println("s" + s);
      }
    });
    return "推送成功";
  }


  @GetMapping("/save")
  public void save() {
    User user = new User();
    user.setId(1234);
//        user.setDateTime(LocalDateTime.now());
    user.setName("哈哈");
    user.setContent("我猜你是ABC");
    List<User> list = new ArrayList<>();
    list.add(user);
    redisService.set("test", list, 30L, TimeUnit.MINUTES);
  }

  @GetMapping("/get")
  public void get() {
    List<User> user = redisService.get("test");
    System.out.println(JsonUtils.toJSONString(user));
  }

  @GetMapping("/save1")
  public void save1() {
    redisService.addZSet("test1", "22", "44", "33", "22");
  }

  @GetMapping("/get1")
  public void get1() {
    Map<Double, Object> value = redisService.popMaxByScoreZSet("test1", 2);
    System.out.println(value);
  }

  @GetMapping("/save2")
  public void save2() {
    redisService.lpushAll("test2", "22", "44", "33", "22");
  }

  @GetMapping("/get2")
  public void get2() {
    String value = redisService.lpop("test2");
    System.out.println(value);
  }

  @GetMapping("/save3")
  public void save3() {
    Object object = redisTemplate.opsForList().leftPop("mmdai", 30, TimeUnit.SECONDS);
  }


  private Date getDate(long millis) {
    Date date = new Date();
    date.setTime(millis);
    return date;
  }


}
