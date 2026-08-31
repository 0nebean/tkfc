package com.tkfc.boot.starter.notice.sender;

import com.tkfc.core.toolkit.PropUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;

/**
 * 邮件发送器
 *
 * @author 0neBean
 * @since 2023-01-04 14:01:23
 */
@Slf4j
public class MailerSender {

    private final JavaMailSender mailSender;
    private final static String SPRING_MAIL_USERNAME = "spring.mail.username";

    public MailerSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 发送html格式邮件
     *
     * @param mailTitle   邮件标题
     * @param mailContent 邮件内容
     * @param toMail      收件邮箱地址
     * @return bool 发送是否成功
     */
    public Boolean sendHtmlMail(String mailTitle, String mailContent, String toMail) {
        try {
            log.info("sendHtmlMail , mailTitle = {} , toMail = {}", mailTitle, toMail);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            String fromUserAccount = PropUtil.getInstance().getConfig(SPRING_MAIL_USERNAME);
            helper.setFrom(fromUserAccount);
            helper.setTo(toMail);
            helper.setSubject(mailTitle);
            helper.setText(mailContent, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("sendHtmlMail got an error  = ", e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
