package com.fa.training.controller;

import com.fa.training.enums.UserStatus;
import com.fa.training.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final PostRepository postRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminDashboardController(UserRepository userRepository, SubjectRepository subjectRepository,
            PostRepository postRepository, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.postRepository = postRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        // Stats
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("activeUsers", userRepository.countByStatus(UserStatus.ACTIVE));
        model.addAttribute("totalSubjects", subjectRepository.count());
        model.addAttribute("totalPosts", postRepository.count());

        // Recent Activity (last 5 logs)
        model.addAttribute("recentLogs",
                auditLogRepository.findAll(PageRequest.of(0, 5, Sort.by("timestamp").descending())).getContent());

        model.addAttribute("currentPage", "dashboard");

        return "admin/dashboard";
    }
}
