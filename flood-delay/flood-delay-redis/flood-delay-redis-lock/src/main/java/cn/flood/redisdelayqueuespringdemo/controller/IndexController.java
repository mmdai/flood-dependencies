package cn.flood.redisdelayqueuespringdemo.controller;

import cn.flood.redisdelayqueuespringdemo.service.TestService;
import cn.flood.redisdelayqueuespringdemo.service.TimeoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @Description TODO
 * @Author daimm
 * @Date 2019/8/1 9:40 AM
 **/
@Controller
@ResponseBody
public class IndexController {


    @Autowired
    private TestService testService;

    @Autowired
    private TimeoutService timeoutService;

    @RequestMapping(value = "/lock")
    public void testLock(){
        timeoutService.foo2();
    }

    @RequestMapping(value = "/idempotent/{param}")
    public void apiIdempotent(@PathVariable("param") String param) throws Exception {

        System.out.println(testService.getValue(param));
    }




}
