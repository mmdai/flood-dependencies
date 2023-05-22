/**
 * <p>Title: RequestWrapper.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author mmdai
 * @date 2019年3月25日
 * @version 1.0
 */
package cn.flood.jwtp.requestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * <p>Title: RequestWrapper</p>  
 * <p>Description: </p>  
 * @author mmdai
 * @date 2019年3月25日
 */
public class RequestWrapper extends HttpServletRequestWrapper {

  private final byte[] body;


  public RequestWrapper(HttpServletRequest request) {
    super(request);
    String sessionStream = getBodyString(request);
    body = sessionStream.getBytes(StandardCharsets.UTF_8);
  }

  /**
   * 获取请求Body
   *
   * @param request
   * @return
   */
  public String getBodyString(final ServletRequest request) {
    StringBuilder sb = new StringBuilder();
    InputStream inputStream = null;
    BufferedReader reader = null;
    try {
      inputStream = cloneInputStream(request.getInputStream());
      reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
      String line = "";
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return sb.toString();
  }

  /**
   * Description: 复制输入流</br>
   *
   * @param inputStream
   * @return</br>
   */
  public InputStream cloneInputStream(ServletInputStream inputStream) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len;
    try {
      while ((len = inputStream.read(buffer)) > -1) {
        byteArrayOutputStream.write(buffer, 0, len);
      }
      byteArrayOutputStream.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    InputStream byteArrayInputStream = new ByteArrayInputStream(
        byteArrayOutputStream.toByteArray());
    return byteArrayInputStream;
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {

    final ByteArrayInputStream bais = new ByteArrayInputStream(body);

    return new ServletInputStream() {

      @Override
      public int read() throws IOException {
        return bais.read();
      }

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
    };
  }

}
