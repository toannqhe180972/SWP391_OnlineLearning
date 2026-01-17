package com.fa.training.controller;

import com.fa.training.entities.Role;
import com.fa.training.entities.User;
import com.fa.training.entities.UserStatus;
import com.fa.training.repository.RoleRepository;
import com.fa.training.repository.UserRepository;
import com.fa.training.service.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AdminUserController(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping
    public String userList(@RequestParam(required = false) String gender,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users = userRepository.findWithFilters(gender, role, status, search, pageable);

        model.addAttribute("users", users);
        model.addAttribute("gender", gender);
        model.addAttribute("role", role);
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("statuses", UserStatus.values());

        return "admin/user-list";
    }

    @GetMapping("/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("statuses", UserStatus.values());
        return "admin/user-detail";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("statuses", UserStatus.values());
        return "admin/user-detail";
    }

    @PostMapping
    public String createUser(@RequestParam String username, @RequestParam String email,
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String gender, @RequestParam String mobile,
            @RequestParam String address, @RequestParam Long roleId,
            @RequestParam UserStatus status, Model model) {
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already exists");
            return "admin/user-detail";
        }

        String randomPassword = UUID.randomUUID().toString().substring(0, 12);
        Role role = roleRepository.findById(roleId).orElseThrow();

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(randomPassword))
                .firstName(firstName)
                .lastName(lastName)
                .gender(gender)
                .phoneNumber(mobile)
                .address(address)
                .status(status)
                .provider("LOCAL")
                .verified(true)
                .build();
        user.getRoles().add(role);

        userRepository.save(user);
        emailService.sendWelcomeEmail(email, username, randomPassword);

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @RequestParam Long roleId,
            @RequestParam UserStatus status) {
        User user = userRepository.findById(id).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();

        user.getRoles().clear();
        user.getRoles().add(role);
        user.setStatus(status);

        userRepository.save(user);
        return "redirect:/admin/users/" + id;
    }
}
