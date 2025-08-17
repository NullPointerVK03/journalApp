package com.VishalSharma.journalApp.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderMail;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public boolean sendMail(String to, String subject, String body) {
        try {
            log.info("Preparing email: from={} to={} subject={}", senderMail, to, subject);

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(senderMail);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);

            javaMailSender.send(mail);

            log.info("Email sent successfully to {}", to);
            return true;

        } catch (Exception e) {
            log.error("Failed to send email to {} with subject={}. Error: {}", to, subject, e.getMessage(), e);
            return false; // instead of throwing, return false (since method returns boolean)
        }
    }
}
