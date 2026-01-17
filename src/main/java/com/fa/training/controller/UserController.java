package com.fa.training.controller;

import com.fa.training.entities.User;
import com.fa.training.repository.UserRepository;
import com.fa.training.service.EmailService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String phoneNumber, @RequestParam String address) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        userRepository.save(user);
        return "redirect:/profile?success";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String handleChangePassword(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String oldPassword, @RequestParam String newPassword,
            Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Incorrect current password!");
            return "change-password";
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Send alert email
        emailService.sendPasswordChangedEmail(user.getEmail());

        return "redirect:/profile?passwordChanged";
    }
}
