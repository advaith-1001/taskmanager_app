package com.adv.taskmanager.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String verificationUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Email Verification for Task Manager");
            message.setText("Click the following link to verify your email: " + verificationUrl);
            message.setFrom("advmca1001@gmail.com");
            System.out.println("Sending email to: " + toEmail);
            mailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}
