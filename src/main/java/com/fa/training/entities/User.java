package com.fa.training.entities;

import com.fa.training.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Column(length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Column(length = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Size(max = 15)
    @Column(length = 15)
    private String phoneNumber;

    @Size(max = 255)
    private String address;

    @Size(max = 500)
    private String avatarUrl;

    private String gender;
    private boolean verified;

    // OAuth2 Fields
    private String googleId;
    private String provider; // LOCAL, GOOGLE
    private LocalDateTime linkedAt;

    private String registrationToken;
    private String resetPasswordToken;
    private LocalDateTime tokenExpiryDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Registration> registrations = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
