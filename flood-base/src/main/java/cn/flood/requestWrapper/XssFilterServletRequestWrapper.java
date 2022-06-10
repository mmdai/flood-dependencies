package cn.flood.requestWrapper;

import cn.flood.Func;
import cn.flood.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerMapping;


import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/**
 * @author mmdai
 * @version 1.0
 * @date 2022/4/14 11:21
 */
@SuppressWarnings("unchecked")
public class XssFilterServletRequestWrapper extends HttpServletRequestWrapper {

    private static Logger log = LoggerFactory.getLogger(XssFilterServletRequestWrapper.class);

    private byte[] requestBody;
    private Charset charSet;

    public XssFilterServletRequestWrapper(HttpServletRequest request) {
        super(request);
        //缓存请求body
        String requestBodyStr = getRequestPostStr(request);
        if(Func.isNotEmpty(requestBodyStr)){
            Map<String, Object> resultJson = JsonUtils.toMap(requestBodyStr);
            Set<String> keySet = resultJson.keySet();
            for (String key : keySet) {
                Object o = resultJson.get(key);
                if (o != null){
                    //过滤掉富文本内的非法标签
                    o =  XSSUtil.clean(o.toString());
                }
                resultJson.put(key,o);
            }
            requestBody = resultJson.toString().getBytes(charSet);
        }

    }

    public String getRequestPostStr(HttpServletRequest request) {
        String charSetStr = request.getCharacterEncoding();
        if (charSetStr == null) {
            charSetStr = "UTF-8";
        }
        charSet = Charset.forName(charSetStr);
        try {
            return StreamUtils.copyToString(request.getInputStream(), charSet);
        } catch (IOException e) {
            log.error("", e);
            return "";
        }
    }


    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return  XSSUtil.clean(value);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return  XSSUtil.clean(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            int length = values.length;
            String[] escapseValues = new String[length];
            for (int i = 0; i < length; i++) {
                escapseValues[i] =   XSSUtil.clean(values[i]);
            }
            return escapseValues;
        }
        return super.getParameterValues(name);
    }

    /**
     * 主要是针对HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE 获取pathvalue的时候把原来的pathvalue经过xss过滤掉
     */
    @Override
    public Object getAttribute(String name) {
        // 获取pathvalue的值
        if (HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE.equals(name)) {
            Map uriTemplateVars = (Map) super.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (Objects.isNull(uriTemplateVars)) {
                return uriTemplateVars;
            }
            Map newMap = new LinkedHashMap<>();
            uriTemplateVars.forEach((key, value) -> {
                if (value instanceof String) {
                    newMap.put(key, XSSUtil.clean((String) value));
                } else {
                    newMap.put(key, value);
                }
            });
            return newMap;
        } else {
            return super.getAttribute(name);
        }
    }
    /**
     * 重写 getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() {
        if (requestBody == null) {
            requestBody = new byte[0];
        }

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    /**
     * 重写 getReader()
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

}
