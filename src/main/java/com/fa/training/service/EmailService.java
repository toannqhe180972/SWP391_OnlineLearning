package com.fa.training.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendWelcomeEmail(String to, String username, String password) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("password", password);
        String content = templateEngine.process("email/welcome-google", context);
        sendHtmlEmail(to, "Welcome to Online Learning Platform", content);
    }

    public void sendAccountLinkedEmail(String to) {
        Context context = new Context();
        String content = templateEngine.process("email/account-linked", context);
        sendHtmlEmail(to, "Google Account Linked Successfully", content);
    }

    public void sendPasswordChangedEmail(String to) {
        Context context = new Context();
        String content = templateEngine.process("email/password-changed", context);
        sendHtmlEmail(to, "Security Alert: Password Changed", content);
    }

    private void sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            // Log for debugging in dev environment
        }
    }
}
