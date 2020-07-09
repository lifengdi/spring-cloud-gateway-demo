package com.lifengdi.gateway.utils;

import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * 发送邮件工具类
 */
public class EmailUtils {

    private static Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    // 登录账户
    private static final String account = "";
    // 登录密码
    private static final String password = "";
    // 服务器地址
    private static final String host = "smtp.exmail.qq.com";
    // 端口
    private static final String port = "465";
    // 协议
    private static final String protocol = "smtp";

    /**
     * 初始化参数
     * @return Session
     */
    private static Session initProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", protocol);
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        // 使用smtp身份验证
        properties.put("mail.smtp.auth", "true");
        // 使用SSL,企业邮箱必需 start
        // 开启安全协议
        MailSSLSocketFactory mailSSLSocketFactory = null;
        try {
            mailSSLSocketFactory = new MailSSLSocketFactory();
            mailSSLSocketFactory.setTrustAllHosts(true);
        } catch (GeneralSecurityException e) {
            logger.error(e.getMessage(), e);
        }
        properties.put("mail.smtp.enable", "true");
        assert mailSSLSocketFactory != null;
        properties.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.put("mail.smtp.socketFactory.port", port);
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, password);
            }
        });
        // 使用SSL,企业邮箱必需 end
        // 显示debug信息 正式环境注释掉
//        session.setDebug(true);
        return session;
    }

    /**
     * 发送邮件（不带附件）
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param receiverList 接收者列表,多个接收者之间用","隔开；抄送者列表使用英文问号"?"隔开
     *                     示例：
     *                     a@kd.cn?b@kd.cn
     *                     a是收件人，b为抄送人
     */
    public static void send(String subject, String content, String receiverList) {
        send(null, subject, content, receiverList);
    }

    /**
     * 发送邮件（不带附件）
     * @param sender 发件人别名
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param receiverList 接收者列表,多个接收者之间用","隔开；抄送者列表使用英文问号"?"隔开
     *                     示例：
     *                     a@kd.cn?b@kd.cn
     *                     a是收件人，b为抄送人
     */
    public static void send(String sender, String subject, String content, String receiverList) {
        sendWithAttach(sender, subject, content, receiverList, null);
    }

    /**
     * 发送邮件（带附件）
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param receiverList 接收者列表,多个接收者之间用","隔开；抄送者列表使用英文问号"?"隔开
     *                     示例：
     *                     a@kd.cn?b@kd.cn
     *                     a是收件人，b为抄送人
     * @param fileSrc 附件地址
     */
    public static void sendWithAttach(String subject, String content, String receiverList, String fileSrc) {
        sendWithAttach(null, subject, content, receiverList, fileSrc);
    }

    /**
     * 发送邮件（带附件）
     * @param sender 发件人别名
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param receiverList 接收者列表,多个接收者之间用","隔开；抄送者列表使用英文问号"?"隔开
     *                     示例：
     *                     a@kd.cn?b@kd.cn
     *                     a是收件人，b为抄送人
     * @param fileSrc 附件地址
     */
    public static void sendWithAttach(String sender, String subject, String content, String receiverList, String fileSrc) {
        List<File> attachments = new ArrayList<>();
        if (StringUtils.isNotBlank(fileSrc)) {
            String[] fileArray = fileSrc.split(",");
            for (String filePath : fileArray) {
                if (StringUtils.isNotBlank(filePath)) {
                    attachments.add(new File(filePath));
                }
            }
        }
        sendWithAttachFile(sender, subject, content, receiverList, attachments);
    }

    /**
     * 发送邮件（带附件）
     * @param sender 发件人别名
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param receiverList 接收者列表,多个接收者之间用","隔开；抄送者列表使用英文问号"?"隔开
     *                     示例：
     *                     a@kd.cn?b@kd.cn
     *                     a是收件人，b为抄送人
     * @param attachments 附件列表
     */
    public static void sendWithAttachFile(String sender, String subject, String content, String receiverList,  List<File> attachments) {
        Objects.requireNonNull(subject, "邮件主题不能为空");
        Objects.requireNonNull(content, "邮件内容不能为空");
        Objects.requireNonNull(receiverList, "邮件接收人不能为空");
        try {
            Session session = initProperties();
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(account, sender));// 发件人,可以设置发件人的别名
            String to = null, cc = null;
            if (receiverList.contains("?")) {
                String[] receiverArray = receiverList.split("\\?");
                to = receiverArray[0];
                cc = receiverArray[1];
            } else {
                to = receiverList;
            }
            Objects.requireNonNull(to, "邮件接收人不能为空");
            // 收件人,多人接收
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            if (StringUtils.isNotBlank(cc)) {
                mimeMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
            }
            // 主题
            mimeMessage.setSubject(subject);
            // 时间
            mimeMessage.setSentDate(new Date());
            // 容器类 附件
            MimeMultipart mimeMultipart = new MimeMultipart();
            // 可以包装文本,图片,附件
            MimeBodyPart bodyPart = new MimeBodyPart();
            // 设置内容
            bodyPart.setContent(content, "text/html; charset=UTF-8");
            mimeMultipart.addBodyPart(bodyPart);
            if (!CollectionUtils.isEmpty(attachments)) {
                // 添加附件
                attachments.forEach(attach -> {
                    MimeBodyPart attachPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(attach);
                    try {
                        attachPart.setDataHandler(new DataHandler(source));
                        attachPart.setFileName(MimeUtility.encodeWord(attach.getName()));
                        mimeMultipart.addBodyPart(attachPart);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                });

            }
            mimeMessage.setContent(mimeMultipart);
            mimeMessage.saveChanges();
            Transport.send(mimeMessage);
        } catch (MessagingException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /*public static void main(String[] args) {

        sendWithAttach("别名", "主题", "123", "lifengdi@kd.cn?lifengdi@lifengdi.com", "F:/image/000.jpg");
        sendWithAttachFile("别名", "主题", "123", "lifengdi@kd.cn?lifengdi@lifengdi.com",
                Arrays.asList(new File("F:/image/000.jpg"), new File("F:/image/001.jpeg")));

        send("主题", "内容", "lifengdi@kd.cn,lifengdi@lifengdi.com");
    }*/

}
