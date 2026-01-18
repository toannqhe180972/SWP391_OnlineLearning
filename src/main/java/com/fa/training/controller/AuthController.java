package com.fa.training.controller;

import com.fa.training.constant.SecurityConstants;
import com.fa.training.constant.ViewConstants;
import com.fa.training.entities.User;
import com.fa.training.enums.AuditAction;
import com.fa.training.message.ErrorMessages;
import com.fa.training.message.SuccessMessages;
import com.fa.training.repository.RoleRepository;
import com.fa.training.repository.UserRepository;
import com.fa.training.service.AuditLogService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/login")
    public String login() {
        return ViewConstants.VIEW_LOGIN;
    }

    @GetMapping("/register")
    public String registerForm() {
        return ViewConstants.VIEW_REGISTER;
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password,
            @RequestParam String email, @RequestParam String firstName,
            @RequestParam String lastName, Model model) {
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", ErrorMessages.USERNAME_EXISTS);
            return ViewConstants.VIEW_REGISTER;
        }
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", ErrorMessages.EMAIL_EXISTS);
            return ViewConstants.VIEW_REGISTER;
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .provider(SecurityConstants.PROVIDER_LOCAL)
                .verified(true)
                .build();

        // Find or create ROLE_USER
        roleRepository.findByName(SecurityConstants.ROLE_USER).ifPresent(user.getRoles()::add);

        userRepository.save(user);
        auditLogService.log(AuditAction.REGISTER.name(), "User", username, "New user self-registered");

        model.addAttribute("message", SuccessMessages.REGISTRATION_SUCCESS);
        return ViewConstants.VIEW_LOGIN;
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm() {
        return ViewConstants.VIEW_RESET_PASSWORD;
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String email, Model model) {
        model.addAttribute("message", SuccessMessages.RESET_LINK_SENT);
        return ViewConstants.VIEW_RESET_PASSWORD;
    }
}
