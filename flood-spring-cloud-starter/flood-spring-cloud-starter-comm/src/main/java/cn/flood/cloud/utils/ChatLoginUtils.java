package cn.flood.cloud.utils;

import cn.flood.LoginUser;
import cn.flood.Func;
import cn.flood.cloud.chat.dto.ChatRequest;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: ChatLoginUtils 聊天登录信息</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * @author mmdai
 * @version 1.0
 * @date 2020/8/2
 */
public class ChatLoginUtils {

    private static final ThreadLocal<Map<String, LoginUser>> THREAD_LOCAL_UER = new ThreadLocal<>();

    @Value("${spring.application.name:default}")
    private String applicationName;

    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";
    private static final String MEMBER = "MEMBER";

    private ChatLoginUtils(){

    }


    /**
     * 清除线程中的用户数据
     **/
    public static void reset() {
        THREAD_LOCAL_UER.remove();
    }

    public void handlerLogin(ChatRequest request, LoginUser userToken) {
        THREAD_LOCAL_UER.remove();
        Map<String, LoginUser> map = THREAD_LOCAL_UER.get();
        if (map == null) {
            map = new HashMap<>();
        }
        // 用户登录信息
        String accessToken = request.getAccessToken();
        if (Func.isNotEmpty(accessToken)) {
            map.put(USER, userToken);
            THREAD_LOCAL_UER.set(map);
        }
        // 管理员登录信息
        String adminAccessToken = request.getAdminAccessToken();
        if (Func.isNotEmpty(adminAccessToken)) {
            map.put(ADMIN, userToken);
            THREAD_LOCAL_UER.set(map);
        }
        // 管理员登录信息
        String memberAccessToken = request.getMemberAccessToken();
        if (Func.isNotEmpty(memberAccessToken)) {
            map.put(MEMBER, userToken);
            THREAD_LOCAL_UER.set(map);
        }
    }

    /**
     * 获取管理员登录信息
     */
    public LoginUser getAdminLoginInfo() {
        Map<String, LoginUser> map = THREAD_LOCAL_UER.get();
        if (map != null && map.get(ADMIN) != null) {
            return map.get(ADMIN);
        }
        return null;
    }

    /**
     * 获取用户登录信息
     */
    public LoginUser getUserLoginInfo() {
        Map<String, LoginUser> map = THREAD_LOCAL_UER.get();
        if (map != null && map.get(USER) != null) {
            return map.get(USER);
        }
        return null;
    }

    /**
     * 获取成员登录信息
     */
    public LoginUser getMemberLoginInfo() {
        Map<String, LoginUser> map = THREAD_LOCAL_UER.get();
        if (map != null && map.get(MEMBER) != null) {
            return map.get(MEMBER);
        }
        return null;
    }
}
