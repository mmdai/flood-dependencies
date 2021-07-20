package cn.flood.redisdelayqueuespringdemo.bo;

import java.time.Clock;

public class Test {

    public static void main(String[] args){
        System.out.println(Clock.systemDefaultZone().millis());
        System.out.println(System.currentTimeMillis());
    }

}
