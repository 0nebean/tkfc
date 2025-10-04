package com.tkfc.boot.starter.notice.config;

import com.tkfc.boot.starter.notice.sender.MailerSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * mailer配置类
 *
 * @author 0neBean
 * @since 2023-01-04 14:13:18
 */
@Configuration
@ConditionalOnProperty("spring.mail.username")
public class MailerConfiguration {


    @Bean
    public MailerSender mailerSender(@Autowired JavaMailSender mailSender){
        return new MailerSender(mailSender);
    }
}
