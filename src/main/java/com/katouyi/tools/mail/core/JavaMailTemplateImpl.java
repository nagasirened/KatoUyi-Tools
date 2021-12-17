package com.katouyi.tools.mail.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.ObjectUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

public class JavaMailTemplateImpl implements MailTemplate {
    private final Logger logger = LoggerFactory.getLogger(JavaMailTemplateImpl.class);
    private JavaMailSender mailSender;
    private MailProperties mailProperties;

    public JavaMailTemplateImpl(JavaMailSender mailSender, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @Override
    public void sendSimpleMail(String to, String subject, String content, String... cc) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getUsername());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        if (ArrayUtil.isNotEmpty(cc)) {
            message.setCc(cc);
        }
        mailSender.send(message);
    }

    @Override
    public void sendHtmlMail(String to, String subject, String content, String... cc) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        buildHelper(to, subject, content, mimeMessage, cc);
        mailSender.send(mimeMessage);
    }

    /*
     * FileSystemResource fsr = new FileSystemResource(new File(filePath));
     * helper.addAttachment(String fileName = filePath.substring(filePath.lastIndexOf(File.separator));, fsr);
     */
    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath, String... cc) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = buildHelper(to, subject, content, mimeMessage, cc);
        File file = FileUtil.file(filePath);
        helper.addAttachment(FileNameUtil.getName(file), file);
        mailSender.send(mimeMessage);
    }

    @Override
    public void sendResourceMail(String to, String subject, String content, String rscPath, String rscId, String... cc) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = buildHelper(to, subject, content, mimeMessage, cc);
        FileSystemResource fsr = new FileSystemResource(new File(rscPath));
        helper.addInline(rscId, fsr);
        mailSender.send(mimeMessage);
    }

    /**
     * 统一封装MimeMessageHelper
     * @param to      收件人地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param message 消息对象
     * @param cc      抄送地址
     * @return MimeMessageHelper
     */
    private MimeMessageHelper buildHelper(String to, String subject, String content, MimeMessage message, String... cc)  throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(mailProperties.getUsername());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        if (!ObjectUtils.isEmpty(cc)) {
            helper.setCc(cc);
        }
        return helper;
    }

}
