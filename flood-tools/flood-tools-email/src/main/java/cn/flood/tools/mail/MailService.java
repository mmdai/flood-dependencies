package cn.flood.tools.mail;

import java.util.List;

public interface MailService {

    public void sendSimpleMail(String[] to, String[] cc, String subject, String content);

    public void sendHtmlMail(String[] to, String[] cc, String subject, String content);

    public void sendAttachmentsMail(String[] to, String[] cc, String subject, String content, String[] filesPath);

    public void sendInlineResourceMail(String[] to, String[] cc, String subject, String content, List<MailAttachment> mailAttachment);

}
