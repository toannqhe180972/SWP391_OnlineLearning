package com.fa.training.controller;

import com.fa.training.constant.PaginationConstants;
import com.fa.training.constant.ViewConstants;
import com.fa.training.entities.AuditLog;
import com.fa.training.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/audit-logs")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public String auditList(@RequestParam(required = false) String performedBy,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AuditLog> auditLogs = auditLogRepository.findWithFilters(performedBy, action, pageable);

        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("performedBy", performedBy);
        model.addAttribute("action", action);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "admin/audit-list";
    }
}
