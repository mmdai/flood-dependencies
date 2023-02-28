package cn.flood.tools.captcha;

import java.io.Serializable;

public interface Token extends Serializable {

  public String getType();

  public void setType(String type);

  public long getDate();

  public void setDate(long date);

  public long getDelay();

  public void setDelay(long delay);

  public String getToken();

  public void setToken(String token);

  public String getBase64Image();

  public boolean isValid(String token);
}
