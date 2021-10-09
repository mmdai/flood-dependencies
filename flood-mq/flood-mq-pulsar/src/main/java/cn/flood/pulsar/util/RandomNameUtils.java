package cn.flood.pulsar.util;

import java.util.Random;

public class RandomNameUtils {

    private static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateProducerName() {
        return "floodP." + generateString(8);
    }

    public static String generateConsumerName() {
        return "floodC." + generateString(8);
    }

    public static String generateString(int len) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            sb.append(allChar.charAt(random.nextInt(allChar.length())));
        }
        return sb.toString();
    }

}
