package com.fa.training.controller;

import com.fa.training.constant.AttributeConstants;
import com.fa.training.constant.PaginationConstants;
import com.fa.training.constant.ViewConstants;
import com.fa.training.entities.Setting;
import com.fa.training.enums.AuditAction;
import com.fa.training.enums.SettingStatus;
import com.fa.training.repository.SettingRepository;
import com.fa.training.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/settings")
public class SettingsController {

    private final SettingRepository settingRepository;
    private final AuditLogService auditLogService;

    public SettingsController(SettingRepository settingRepository, AuditLogService auditLogService) {
        this.settingRepository = settingRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public String settingsList(@RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "" + PaginationConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = "" + PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_SORT_DIRECTION) String sortDir,
            Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        SettingStatus settingStatus = (status != null && !status.isEmpty()) ? SettingStatus.valueOf(status) : null;
        Page<Setting> settings = settingRepository.findWithFilters(type, settingStatus, search, pageable);

        model.addAttribute(AttributeConstants.ATTR_SETTINGS, settings);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute(AttributeConstants.ATTR_STATUSES, SettingStatus.values());

        return ViewConstants.VIEW_ADMIN_SETTINGS_LIST;
    }

    @GetMapping("/{id}")
    public String settingDetail(@PathVariable Long id, Model model) {
        Setting setting = settingRepository.findById(id).orElseThrow();
        model.addAttribute(AttributeConstants.ATTR_SETTING, setting);
        model.addAttribute(AttributeConstants.ATTR_STATUSES, SettingStatus.values());
        return ViewConstants.VIEW_ADMIN_SETTINGS_DETAIL;
    }

    @GetMapping("/new")
    public String newSettingForm(Model model) {
        model.addAttribute(AttributeConstants.ATTR_STATUSES, SettingStatus.values());
        return ViewConstants.VIEW_ADMIN_SETTINGS_DETAIL;
    }

    @PostMapping
    public String createSetting(@RequestParam String type, @RequestParam String value,
            @RequestParam Integer order, @RequestParam String description,
            @RequestParam SettingStatus status) {
        Setting setting = Setting.builder()
                .type(type)
                .value(value)
                .order(order)
                .description(description)
                .status(status)
                .build();
        settingRepository.save(setting);
        auditLogService.log(AuditAction.CREATE_SETTING.name(), "Setting", type, "Value: " + value);
        return ViewConstants.REDIRECT_ADMIN_SETTINGS;
    }

    @PostMapping("/{id}")
    public String updateSetting(@PathVariable Long id, @RequestParam String type,
            @RequestParam String value, @RequestParam Integer order,
            @RequestParam String description, @RequestParam SettingStatus status) {
        Setting setting = settingRepository.findById(id).orElseThrow();
        setting.setType(type);
        setting.setValue(value);
        setting.setOrder(order);
        setting.setDescription(description);
        setting.setStatus(status);
        settingRepository.save(setting);
        auditLogService.log(AuditAction.UPDATE_SETTING.name(), "Setting", setting.getId().toString(),
                "Updated by Admin");
        return ViewConstants.REDIRECT_ADMIN_SETTINGS + "/" + id;
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id) {
        Setting setting = settingRepository.findById(id).orElseThrow();
        setting.setStatus(setting.getStatus() == SettingStatus.ACTIVE ? SettingStatus.INACTIVE : SettingStatus.ACTIVE);
        settingRepository.save(setting);
        auditLogService.log(AuditAction.TOGGLE_SETTING.name(), "Setting", setting.getId().toString(),
                "New status: " + setting.getStatus());
        return ViewConstants.REDIRECT_ADMIN_SETTINGS;
    }
}
