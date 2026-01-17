package com.fa.training.service;

import com.fa.training.entities.Role;
import com.fa.training.entities.User;
import com.fa.training.repository.RoleRepository;
import com.fa.training.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
                user.setProvider("GOOGLE");
                user.setLinkedAt(LocalDateTime.now());
                userRepository.save(user);
                emailService.sendAccountLinkedEmail(email);
            }
        } else {
            // Register new user
            user = registerNewOAuth2User(email, name, googleId);
        }

        return oAuth2User;
    }

    private User registerNewOAuth2User(String email, String name, String googleId) {
        String randomUsername = "google_" + UUID.randomUUID().toString().substring(0, 8);
        String randomPassword = UUID.randomUUID().toString().substring(0, 12);

        String[] nameParts = name.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = (nameParts.length > 1) ? nameParts[1] : "";

        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();

        User user = User.builder()
                .username(randomUsername)
                .email(email)
                .password(passwordEncoder.encode(randomPassword))
                .firstName(firstName)
                .lastName(lastName)
                .provider("GOOGLE")
                .googleId(googleId)
                .linkedAt(LocalDateTime.now())
                .verified(true)
                .roles(Collections.singleton(userRole))
                .build();

        User savedUser = userRepository.save(user);

        // Send email with credentials
        emailService.sendWelcomeEmail(email, randomUsername, randomPassword);

        return savedUser;
    }
}
