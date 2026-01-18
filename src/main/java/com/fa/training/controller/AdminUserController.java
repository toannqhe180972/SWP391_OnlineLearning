package com.fa.training.controller;

import com.fa.training.constant.AttributeConstants;
import com.fa.training.constant.PaginationConstants;
import com.fa.training.constant.SecurityConstants;
import com.fa.training.constant.ViewConstants;
import com.fa.training.entities.Role;
import com.fa.training.entities.User;
import com.fa.training.enums.AuditAction;
import com.fa.training.enums.UserStatus;
import com.fa.training.message.ErrorMessages;
import com.fa.training.repository.RoleRepository;
import com.fa.training.repository.UserRepository;
import com.fa.training.service.AuditLogService;
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
    private final AuditLogService auditLogService;

    public AdminUserController(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, EmailService emailService, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public String userList(@RequestParam(required = false) String gender,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "" + PaginationConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = "" + PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_SORT_DIRECTION) String sortDir,
            Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        UserStatus userStatus = (status != null && !status.isEmpty()) ? UserStatus.valueOf(status) : null;
        Page<User> users = userRepository.findWithFilters(gender, role, userStatus, search, pageable);

        model.addAttribute(AttributeConstants.ATTR_USERS, users);
        model.addAttribute("gender", gender);
        model.addAttribute("role", role);
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute(AttributeConstants.ATTR_STATUSES, UserStatus.values());

        return ViewConstants.VIEW_ADMIN_USER_LIST;
    }

    @GetMapping("/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute(AttributeConstants.ATTR_USER, user);
        model.addAttribute(AttributeConstants.ATTR_ROLES, roleRepository.findAll());
        model.addAttribute(AttributeConstants.ATTR_STATUSES, UserStatus.values());
        return ViewConstants.VIEW_ADMIN_USER_DETAIL;
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute(AttributeConstants.ATTR_ROLES, roleRepository.findAll());
        model.addAttribute(AttributeConstants.ATTR_STATUSES, UserStatus.values());
        return ViewConstants.VIEW_ADMIN_USER_DETAIL;
    }

    @PostMapping
    public String createUser(@RequestParam String username, @RequestParam String email,
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String gender, @RequestParam String mobile,
            @RequestParam String address, @RequestParam Long roleId,
            @RequestParam UserStatus status, Model model) {
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", ErrorMessages.USERNAME_EXISTS);
            return ViewConstants.VIEW_ADMIN_USER_DETAIL;
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
                .provider(SecurityConstants.PROVIDER_LOCAL)
                .verified(true)
                .build();
        user.getRoles().add(role);

        userRepository.save(user);
        auditLogService.log(AuditAction.CREATE_USER.name(), "User", user.getUsername(), "Created by Admin");
        emailService.sendWelcomeEmail(email, username, randomPassword);

        return ViewConstants.REDIRECT_ADMIN_USERS;
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @RequestParam Long roleId,
            @RequestParam UserStatus status) {
        User user = userRepository.findById(id).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();

        user.getRoles().clear();
        user.getRoles().add(role);
        user.setStatus(status);

        String details = String.format("Admin updated user role to %s and status to %s", role.getName(), status);
        userRepository.save(user);
        auditLogService.log(AuditAction.UPDATE_USER.name(), "User", user.getUsername(), details);
        return ViewConstants.REDIRECT_ADMIN_USERS + "/" + id;
    }
}
