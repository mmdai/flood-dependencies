package cn.flood.tools.mail;

import java.io.File;
import java.util.List;
import java.util.Properties;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


@Component
public class MailServiceImpl implements MailService, InitializingBean {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Value("${mail.host}")
  private String host;

  @Value("${mail.username}")
  private String username;

  @Value("${mail.password}")
  private String password;

  @Value("${mail.from.addr}")
  private String from;

  private JavaMailSenderImpl mailSender;

  /**
   * 发送文本邮件
   *
   * @param to
   * @param cc
   * @param subject
   * @param content
   */
  @Override
  public void sendSimpleMail(String[] to, String[] cc, String subject, String content) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(to);
    if (cc != null && cc.length > 0) {
      message.setCc(cc);
    }
    message.setSubject(subject);
    message.setText(content);

    try {
      mailSender.send(message);
      logger.info("mail for simple：send an mail success");
    } catch (Exception e) {
      logger.error("mail for simple：send an mail error", e);
    }

  }

  /**
   * 发送html邮件
   *
   * @param to
   * @param cc
   * @param subject
   * @param content
   */
  @Override
  public void sendHtmlMail(String[] to, String[] cc, String subject, String content) {
    MimeMessage message = mailSender.createMimeMessage();

    try {
      //true表示需要创建一个multipart message
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(from);
      helper.setTo(to);
      if (cc != null && cc.length > 0) {
        helper.setCc(cc);
      }
      helper.setSubject(subject);
      helper.setText(content, true);

      mailSender.send(message);
      logger.info("mail for html：send an mail success");
    } catch (Exception e) {
      logger.error("mail for html：send an mail error", e);
    }
  }


  /**
   * 发送带附件的邮件
   *
   * @param to
   * @param cc
   * @param subject
   * @param content
   * @param filesPath
   */
  @Override
  public void sendAttachmentsMail(String[] to, String[] cc, String subject, String content,
      String[] filesPath) {
    MimeMessage message = mailSender.createMimeMessage();

    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(from);
      helper.setTo(to);
      if (cc != null && cc.length > 0) {
        helper.setCc(cc);
      }
      helper.setSubject(subject);
      helper.setText(content, true);

      if (filesPath != null && filesPath.length > 0) {
        for (int i = 0; i < filesPath.length; i++) {

          String path = filesPath[i];
          FileSystemResource file = new FileSystemResource(new File(path));
          String fileName = path.substring(path.lastIndexOf(File.separator));
          helper.addAttachment(fileName, file);
        }
      }

      mailSender.send(message);
      logger.info("mail for attachment：send an mail success");
    } catch (Exception e) {
      logger.error("mail for attachment：send an mail error", e);
    }
  }


  /**
   * 发送正文中有静态资源（图片）的邮件
   *
   * @param to
   * @param cc
   * @param subject
   * @param content
   * @param mailAttachment
   */
  @Override
  public void sendInlineResourceMail(String[] to, String[] cc, String subject, String content,
      List<MailAttachment> mailAttachment) {
    MimeMessage message = mailSender.createMimeMessage();

    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(from);
      helper.setTo(to);
      if (cc != null && cc.length > 0) {
        helper.setCc(cc);
      }
      helper.setSubject(subject);
      helper.setText(content, true);

      if (mailAttachment != null && mailAttachment.size() > 0) {

        for (int i = 0; i < mailAttachment.size(); i++) {

          MailAttachment ma = mailAttachment.get(i);
          if (ma.getAttachmentType() == AttachmentType.FILE) {

            FileSystemResource res = new FileSystemResource(new File(ma.getPath()));
            helper.addInline(ma.getId(), res);
          } else if (ma.getAttachmentType() == AttachmentType.STREAM) {
            helper.addInline(ma.getId(), ma.getInputStreamSource(), ma.getContentType());
          } else {

            logger.error("mail_attachment_type_error");
            return;
          }

        }
      }

      mailSender.send(message);
      logger.info("mail for inlineresource：send an mail success");
    } catch (Exception e) {
      logger.error("mail for inlineresource：send an mail error", e);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setUsername(username);
    mailSender.setPassword(password);
    Properties properties = new Properties();
    properties.put("mail.smtp.auth", true);
    properties.put("mail.smtp.connectiontimeout", 500);
    properties.put("mail.smtp.timeout", 3000);
    properties.put("mail.smtp.socketFactory.fallback", false);
    mailSender.setJavaMailProperties(properties);

  }
}
