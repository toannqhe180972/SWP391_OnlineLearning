package com.fa.training.controller;

import com.fa.training.constant.ViewConstants;
import com.fa.training.entities.User;
import com.fa.training.message.ErrorMessages;
import com.fa.training.repository.UserRepository;
import com.fa.training.service.EmailService;
import com.fa.training.service.FileUploadService;
import com.fa.training.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final FileUploadService fileUploadService;
    private final UserService userService;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder,
            EmailService emailService, FileUploadService fileUploadService, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.fileUploadService = fileUploadService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        return ViewConstants.VIEW_PROFILE;
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String phoneNumber, @RequestParam String address,
            @RequestParam String gender) {
        userService.updateProfile(userDetails.getUsername(), firstName, lastName, phoneNumber, address, gender);
        return ViewConstants.REDIRECT_PROFILE + "?success";
    }

    @PostMapping("/profile/avatar")
    public String uploadAvatar(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("avatar") MultipartFile file) {
        try {
            String avatarUrl = fileUploadService.uploadAvatar(file);
            userService.updateAvatar(userDetails.getUsername(), avatarUrl);
            return ViewConstants.REDIRECT_PROFILE + "?avatarSuccess";
        } catch (Exception e) {
            return ViewConstants.REDIRECT_PROFILE + "?avatarError=" + e.getMessage();
        }
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return ViewConstants.VIEW_CHANGE_PASSWORD;
    }

    @PostMapping("/change-password")
    public String handleChangePassword(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String oldPassword, @RequestParam String newPassword,
            Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", ErrorMessages.PASSWORD_INCORRECT);
            return ViewConstants.VIEW_CHANGE_PASSWORD;
        }

        userService.changePassword(userDetails.getUsername(), passwordEncoder.encode(newPassword));
        emailService.sendPasswordChangedEmail(user.getEmail());

        return ViewConstants.REDIRECT_PROFILE + "?passwordChanged";
    }
}
