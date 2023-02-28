package cn.flood.tools.mail;

import java.io.Serializable;
import org.springframework.core.io.InputStreamSource;

/**
 * TODO mail attachment entity
 * <p>
 * Created on 2017年6月19日
 * <p>
 *
 * @author mmdai
 * @date 2017年6月19日
 */

public class MailAttachment implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -4414263261026526236L;

  public String id;
  public AttachmentType attachmentType;
  public String path;
  public InputStreamSource inputStreamSource;
  public String contentType;


  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public AttachmentType getAttachmentType() {
    return attachmentType;
  }

  public void setAttachmentType(AttachmentType attachmentType) {
    this.attachmentType = attachmentType;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public InputStreamSource getInputStreamSource() {
    return inputStreamSource;
  }

  public void setInputStreamSource(InputStreamSource inputStreamSource) {
    this.inputStreamSource = inputStreamSource;
  }

}
