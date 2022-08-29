package com.blogswebsite.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;


@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")//将配置文件中的发件人注入
    private String from;

    public void sendMail(String to, String subject, String content){

        try{
            MimeMessage message = mailSender.createMimeMessage();//邮件发送对象
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);//邮件构建协助对象
            messageHelper.setFrom(from);//发送者
            messageHelper.setTo(to);//接收者
            messageHelper.setSubject(subject);//主题
            messageHelper.setText(content,true);//内容，加true表示html文件
            mailSender.send(messageHelper.getMimeMessage());//发送
        }catch (Exception e){
            logger.error("发送邮件失败"+e.getMessage());
        }

    }
}
