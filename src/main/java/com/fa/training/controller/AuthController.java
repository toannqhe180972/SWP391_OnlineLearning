package com.fa.training.controller;

import com.fa.training.constant.SecurityConstants;
import com.fa.training.constant.ViewConstants;
import com.fa.training.entities.User;
import com.fa.training.enums.AuditAction;
import com.fa.training.enums.UserStatus;
import com.fa.training.message.ErrorMessages;
import com.fa.training.message.SuccessMessages;
import com.fa.training.repository.RoleRepository;
import com.fa.training.repository.UserRepository;
import com.fa.training.service.AuditLogService;
import com.fa.training.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final UserService userService;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, AuditLogService auditLogService, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
        this.userService = userService;
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
            @RequestParam String lastName, Model model, RedirectAttributes redirectAttributes) {

        try {
            // Validation
            if (username.trim().length() < 3) {
                model.addAttribute("error", "Username must be at least 3 characters");
                model.addAttribute("username", username);
                model.addAttribute("email", email);
                model.addAttribute("firstName", firstName);
                model.addAttribute("lastName", lastName);
                return ViewConstants.VIEW_REGISTER;
            }

            if (password.trim().length() < 6) {
                model.addAttribute("error", "Password must be at least 6 characters");
                model.addAttribute("username", username);
                model.addAttribute("email", email);
                model.addAttribute("firstName", firstName);
                model.addAttribute("lastName", lastName);
                return ViewConstants.VIEW_REGISTER;
            }

            if (userRepository.findByUsername(username).isPresent()) {
                model.addAttribute("error", ErrorMessages.USERNAME_EXISTS);
                model.addAttribute("email", email);
                model.addAttribute("firstName", firstName);
                model.addAttribute("lastName", lastName);
                return ViewConstants.VIEW_REGISTER;
            }
            if (userRepository.findByEmail(email).isPresent()) {
                model.addAttribute("error", ErrorMessages.EMAIL_EXISTS);
                model.addAttribute("username", username);
                model.addAttribute("firstName", firstName);
                model.addAttribute("lastName", lastName);
                return ViewConstants.VIEW_REGISTER;
            }

            User user = User.builder()
                    .username(username.trim())
                    .password(passwordEncoder.encode(password))
                    .email(email.trim())
                    .firstName(firstName.trim())
                    .lastName(lastName.trim())
                    .provider(SecurityConstants.PROVIDER_LOCAL)
                    .verified(true)
                    .status(UserStatus.ACTIVE)
                    .build();

            // Ensure ROLE_USER is assigned
            roleRepository.findByName(SecurityConstants.ROLE_USER).ifPresent(user.getRoles()::add);

            // Save user with transaction
            User savedUser = userService.registerNewUser(user);
            auditLogService.log(AuditAction.REGISTER.name(), "User", savedUser.getUsername(),
                    "New user self-registered");

            redirectAttributes.addFlashAttribute("message", SuccessMessages.REGISTRATION_SUCCESS);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            return ViewConstants.VIEW_REGISTER;
        }
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
