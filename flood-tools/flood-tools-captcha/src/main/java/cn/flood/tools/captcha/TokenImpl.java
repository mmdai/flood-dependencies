package cn.flood.tools.captcha;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.Clock;

public class TokenImpl implements Token {

  public static final String DEFAULT_TYPE = "VALIDATE_TOKEN";
  private static final long serialVersionUID = -4225747109239671522L;
  private String type;

  private String token;

  private long date;

  private long delay;

  private String base64Image;

  public TokenImpl(String type, String token, long date, long delay) {
    this.type = type;
    this.token = token;
    this.date = date;
    this.delay = delay;
  }

  public TokenImpl(String token, long date, long delay) {
    this.type = DEFAULT_TYPE;
    this.token = token;
    this.date = date;
    this.delay = delay;
  }

  public TokenImpl(String token, long date, long delay, String image) {
    this.type = DEFAULT_TYPE;
    this.token = token;
    this.date = date;
    this.delay = delay;
    this.base64Image = image;
  }

  public TokenImpl() {
    super();
  }

  @JsonBackReference
  @Override
  public String getBase64Image() {
    return base64Image;
  }

  @JsonBackReference
  public void setBase64Image(String image) {
    this.base64Image = image;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String getToken() {
    return token;
  }

  @Override
  public void setToken(String uid) {
    this.token = uid;
  }

  @Override
  public long getDate() {
    return date;
  }

  @Override
  public void setDate(long date) {
    this.date = date;
  }

  @Override
  public long getDelay() {
    return delay;
  }

  @Override
  public void setDelay(long delay) {
    this.delay = delay;
  }

  @Override
  public boolean isValid(String token) {
    if (token.equalsIgnoreCase(this.token)) {
      return Clock.systemDefaultZone().millis() - this.date < this.delay;
    }
    return false;
  }

  public void setToken(String token, long date, long delay) {
    this.type = DEFAULT_TYPE;
    this.token = token;
    this.date = date;
    this.delay = delay;
  }

  public void setToken(String token, long date, long delay, String image) {
    this.type = DEFAULT_TYPE;
    this.token = token;
    this.date = date;
    this.delay = delay;
    this.base64Image = image;
  }

}
