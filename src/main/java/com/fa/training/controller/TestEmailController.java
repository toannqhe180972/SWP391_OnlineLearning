package com.fa.training.controller;

import com.fa.training.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestEmailController {

    private final EmailService emailService;

    public TestEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/admin/test-email")
    public String testEmailForm() {
        return "test-email";
    }

    @PostMapping("/admin/test-email")
    public String sendTestEmail(@RequestParam String email, Model model) {
        try {
            emailService.sendWelcomeEmail(email, "test_user_demo", "DemoPassword123");
            model.addAttribute("success", "Email sent successfully to " + email);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to send email: " + e.getMessage());
        }
        return "test-email";
    }
}
