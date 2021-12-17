package com.katouyi.tools.mail.config;

import com.katouyi.tools.mail.core.JavaMailTemplateImpl;
import com.katouyi.tools.mail.core.MailTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;

@Configuration
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
public class MainConfiguration {

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private MailProperties mailProperties;

    @Bean
    @ConditionalOnBean({JavaMailSender.class, MailProperties.class})
    public MailTemplate mailTemplate() {
        return new JavaMailTemplateImpl(javaMailSender, mailProperties);
    }
}
