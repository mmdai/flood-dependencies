package cn.flood.cloud.utils;

import cn.flood.date.DateUtils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>Title: RandomUtils</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * @author mmdai
 * @version 1.0
 * @date 2020/8/2
 */
public class RandomUtils {

    /**
     * 生成订单编号-方式一
     * @return
     */
    public static String generateOrderCode(){
        //TODO:时间戳+N为随机数流水号
        return DateUtils.getCurrentFormatDate("yyyyMMddHHmmssSS") + generateNumber(6);
    }

    //num为随机数流水号
    public static String generateNumber(final int num){
        StringBuffer sb = new StringBuffer();
        for (int i=1;i<=num;i++){
            sb.append(ThreadLocalRandom.current().nextInt(9));
        }
        return sb.toString();

    }

//    public static void main(String[]  args) {
//        for(int i=0 ;1<500;i++){
//            new Thread(()-> System.out.println(generateOrderCode())).start();
//        }
//    }
}
