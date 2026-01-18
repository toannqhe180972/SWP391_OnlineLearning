package com.fa.training.service;

import com.fa.training.entities.User;
import com.fa.training.enums.UserStatus;
import com.fa.training.repository.RoleRepository;
import com.fa.training.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fa.training.constant.SecurityConstants;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User registerNewUser(User user) {
        // Ensure status is set
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }

        // Ensure role is assigned
        if (user.getRoles().isEmpty()) {
            roleRepository.findByName(SecurityConstants.ROLE_USER).ifPresent(user.getRoles()::add);
        }

        // Save and flush to ensure immediate persistence
        return userRepository.saveAndFlush(user);
    }

    @Transactional
    public User updateProfile(String username, String firstName, String lastName, String phoneNumber, String address,
            String gender) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        user.setGender(gender);
        return userRepository.saveAndFlush(user);
    }

    @Transactional
    public User updateAvatar(String username, String avatarUrl) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatarUrl(avatarUrl);
        return userRepository.saveAndFlush(user);
    }

    @Transactional
    public void changePassword(String username, String encodedPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(encodedPassword);
        userRepository.saveAndFlush(user);
    }
}
