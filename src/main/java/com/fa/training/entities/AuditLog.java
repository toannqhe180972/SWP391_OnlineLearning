package com.fa.training.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // e.g., LOGIN, UPDATE_USER, CHANGE_SETTING

    @Column(nullable = false)
    private String entityName; // e.g., User, Setting

    private String entityId;

    @Column(nullable = false)
    private String performedBy; // username

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String details;

    private String ipAddress;
}
