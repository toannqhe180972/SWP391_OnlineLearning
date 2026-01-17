package com.fa.training.utils;

import com.fa.training.entities.*;
import com.fa.training.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SubjectRepository subjectRepository;
    private final PostRepository postRepository;
    private final SliderRepository sliderRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository,
            SubjectRepository subjectRepository, PostRepository postRepository,
            SliderRepository sliderRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.subjectRepository = subjectRepository;
        this.postRepository = postRepository;
        this.sliderRepository = sliderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initData();
    }

    private void initData() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            return roleRepository.save(role);
        });

        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_USER");
            return roleRepository.save(role);
        });

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("System")
                    .lastName("Admin")
                    .email("admin@onlinelearning.com")
                    .verified(true)
                    .provider("LOCAL")
                    .roles(new HashSet<>(Collections.singletonList(adminRole)))
                    .build();
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User user = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .firstName("Regular")
                    .lastName("User")
                    .email("user@onlinelearning.com")
                    .verified(true)
                    .provider("LOCAL")
                    .roles(new HashSet<>(Collections.singletonList(userRole)))
                    .build();
            userRepository.save(user);
        }

        if (sliderRepository.count() == 0) {
            Slider s1 = new Slider(null, "Welcome", "/images/slider1.jpg", "/subjects", "Active");
            sliderRepository.save(s1);
        }

        if (subjectRepository.count() == 0) {
            Subject sub1 = new Subject(null, "Java", "/images/java.jpg", "Learn Java", "Expert content", true);
            subjectRepository.save(sub1);
        }

        if (postRepository.count() == 0) {
            Post p1 = new Post(null, "News", "/images/news1.jpg", LocalDateTime.now(), "Content", true);
            postRepository.save(p1);
        }
    }
}
