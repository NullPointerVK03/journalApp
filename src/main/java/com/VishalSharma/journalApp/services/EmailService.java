package com.VishalSharma.journalApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderMail;


    public boolean sendMail(String to, String subject, String body){
        try{
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(senderMail);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            javaMailSender.send(mail);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
