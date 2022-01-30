package cn.flood.constants;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/1/30 11:39
 */
public interface CoreConstant {

    /**
     * 错误码字典项
     */
    interface HTTP_ERROR_CODE {
        String A00500 = "A00500";//其他异常
        String A00501 = "A00501";//连接超时
        String A00502 = "A00502";//超时响应
    }
}
