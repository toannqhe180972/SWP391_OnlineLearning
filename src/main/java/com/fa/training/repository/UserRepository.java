package com.fa.training.repository;

import com.fa.training.entities.User;
import com.fa.training.entities.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(:gender IS NULL OR u.gender = :gender) AND " +
            "(:role IS NULL OR :role MEMBER OF u.roles) AND " +
            "(:status IS NULL OR u.status = :status) AND " +
            "(:search IS NULL OR " +
            "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "u.phoneNumber LIKE CONCAT('%', :search, '%'))")
    Page<User> findWithFilters(@Param("gender") String gender,
            @Param("role") String role,
            @Param("status") UserStatus status,
            @Param("search") String search,
            Pageable pageable);
}
