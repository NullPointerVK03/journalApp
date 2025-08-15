package com.VishalSharma.journalApp.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderMail;

    public boolean sendMail(String to, String subject, String body) {
        try {
            log.info("Preparing email to send from {} to {}", senderMail, to);
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(senderMail);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            log.info("Sending email to {}", to);
            javaMailSender.send(mail);
            log.info("Email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.error("Error occurred while sending email to {} with subject {}", to, subject, e);
            throw new RuntimeException(e);
        }
    }
}
