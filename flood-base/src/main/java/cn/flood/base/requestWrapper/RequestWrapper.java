/**
 * <p>Title: RequestWrapper.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author mmdai
 * @date 2019年3月25日
 * @version 1.0
 */
package cn.flood.base.requestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

/**
 * <p>Title: RequestWrapper</p>
 * <p>Description: </p>
 * @author mmdai
 * @date 2019年3月25日
 */
public class RequestWrapper extends HttpServletRequestWrapper {

  private static Logger log = LoggerFactory.getLogger(RequestWrapper.class);

  private byte[] requestBody;

  private Charset charSet;


  public RequestWrapper(HttpServletRequest request) {
    super(request);
    String requestBodyStr = getRequestPostStr(request);
    requestBody = requestBodyStr.getBytes(StandardCharsets.UTF_8);
  }

  public String getRequestPostStr(HttpServletRequest request) {
    String charSetStr = request.getCharacterEncoding();
    if (charSetStr == null) {
      charSetStr = StandardCharsets.UTF_8.name();
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
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
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


}
