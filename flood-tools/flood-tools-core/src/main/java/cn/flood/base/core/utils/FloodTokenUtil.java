package cn.flood.base.core.utils;

import cn.flood.base.core.UserToken;
import cn.flood.base.core.http.WebUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author mmdai
 * @version 1.0.0
 * @ClassName FloodTokenUtil
 * @Description
 * @createTime 2024年07月31日 08:59
 */
public class FloodTokenUtil {

    /**
     * 获取用户信息
     *
     * @return User
     */
    public static UserToken getUser() {
        HttpServletRequest request = WebUtil.getRequest();
        if (request == null) {
            return null;
        }
        // 优先从 request 中获取
        Object floodUser = request.getAttribute(WebUtil.REQUEST_TOKEN_NAME);
        return (UserToken) floodUser;
    }

}
