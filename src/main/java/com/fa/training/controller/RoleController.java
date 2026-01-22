package com.fa.training.controller;

import com.fa.training.entities.Role;
import com.fa.training.enums.AuditAction;
import com.fa.training.repository.RoleRepository;
import com.fa.training.service.AuditLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/roles")
public class RoleController {

    private final RoleRepository roleRepository;
    private final AuditLogService auditLogService;

    public RoleController(RoleRepository roleRepository, AuditLogService auditLogService) {
        this.roleRepository = roleRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public String roleList(Model model) {
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        model.addAttribute("currentPage", "roles");
        return "admin/role-list";
    }

    @GetMapping("/new")
    public String newRoleForm(Model model) {
        model.addAttribute("currentPage", "roles");
        return "admin/role-detail";
    }

    @GetMapping("/{id}")
    public String roleDetail(@PathVariable Long id, Model model) {
        Role role = roleRepository.findById(id).orElseThrow();
        model.addAttribute("role", role);
        model.addAttribute("userCount", role.getUsers().size());
        model.addAttribute("currentPage", "roles");
        return "admin/role-detail";
    }

    @PostMapping
    public String createRole(@RequestParam String name, @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        // Check if role name already exists
        if (roleRepository.findByName(name).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Role name already exists!");
            return "redirect:/admin/roles/new";
        }

        // Ensure role name starts with ROLE_
        String roleName = name.toUpperCase();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        Role role = Role.builder()
                .name(roleName)
                .description(description)
                .build();
        roleRepository.save(role);

        auditLogService.log(AuditAction.CREATE.name(), "Role", roleName, "Created new role");
        redirectAttributes.addFlashAttribute("success", "Role created successfully!");
        return "redirect:/admin/roles";
    }

    @PostMapping("/{id}")
    public String updateRole(@PathVariable Long id, @RequestParam String name,
            @RequestParam(required = false) String description, RedirectAttributes redirectAttributes) {
        Role role = roleRepository.findById(id).orElseThrow();

        // Check if new name conflicts with existing role (excluding current)
        roleRepository.findByName(name).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Role name already exists!");
            }
        });

        String oldName = role.getName();
        role.setName(name);
        role.setDescription(description);
        roleRepository.save(role);

        auditLogService.log(AuditAction.UPDATE.name(), "Role", name,
                "Updated role from " + oldName + " to " + name);
        redirectAttributes.addFlashAttribute("success", "Role updated successfully!");
        return "redirect:/admin/roles/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Role role = roleRepository.findById(id).orElseThrow();

        // Don't allow deleting roles that have users
        if (!role.getUsers().isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Cannot delete role with " + role.getUsers().size() + " assigned users!");
            return "redirect:/admin/roles/" + id;
        }

        // Don't allow deleting ROLE_ADMIN or ROLE_USER (system roles)
        if (role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_USER")) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete system roles!");
            return "redirect:/admin/roles/" + id;
        }

        String roleName = role.getName();
        roleRepository.delete(role);

        auditLogService.log(AuditAction.DELETE.name(), "Role", roleName, "Deleted role");
        redirectAttributes.addFlashAttribute("success", "Role deleted successfully!");
        return "redirect:/admin/roles";
    }
}
