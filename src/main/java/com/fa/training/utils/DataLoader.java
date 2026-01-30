package com.fa.training.utils;

import com.fa.training.constant.SecurityConstants;
import com.fa.training.entities.*;
import com.fa.training.enums.SettingStatus;
import com.fa.training.enums.UserStatus;
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
    private final SettingRepository settingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository,
            SubjectRepository subjectRepository, PostRepository postRepository,
            SliderRepository sliderRepository, SettingRepository settingRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.subjectRepository = subjectRepository;
        this.postRepository = postRepository;
        this.sliderRepository = sliderRepository;
        this.settingRepository = settingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initData();
    }

    private void initData() {
        Role adminRole = roleRepository.findByName(SecurityConstants.ROLE_ADMIN).orElseGet(() -> {
            Role role = new Role();
            role.setName(SecurityConstants.ROLE_ADMIN);
            return roleRepository.save(role);
        });

        Role userRole = roleRepository.findByName(SecurityConstants.ROLE_USER).orElseGet(() -> {
            Role role = new Role();
            role.setName(SecurityConstants.ROLE_USER);
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
                    .provider(SecurityConstants.PROVIDER_LOCAL)
                    .status(UserStatus.ACTIVE)
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
                    .provider(SecurityConstants.PROVIDER_LOCAL)
                    .status(UserStatus.ACTIVE)
                    .roles(new HashSet<>(Collections.singletonList(userRole)))
                    .build();
            userRepository.save(user);
        }

        if (sliderRepository.count() == 0) {
            Slider s1 = Slider.builder()
                    .title("Welcome")
                    .image("/images/slider1.jpg")
                    .backlink("/subjects")
                    .status("Active")
                    .build();
            sliderRepository.save(s1);
        }

        if (subjectRepository.count() == 0) {
            Subject sub1 = Subject.builder()
                    .title("Java")
                    .thumbnail("/images/java.jpg")
                    .tagline("Learn Java")
                    .description("Expert content")
                    .featured(true)
                    .build();
            subjectRepository.save(sub1);
        }

        if (postRepository.count() == 0) {
            Post p1 = Post.builder()
                    .title("News")
                    .thumbnail("/images/news1.jpg")
                    .postDate(LocalDateTime.now())
                    .content("Content")
                    .hot(true)
                    .build();
            postRepository.save(p1);
        }

        // Init Settings
        if (settingRepository.count() == 0) {
            Setting s1 = Setting.builder().type("SUBJECT_CATEGORY").value("Programming").order(1)
                    .description("Programming subjects").status(SettingStatus.ACTIVE).build();
            Setting s2 = Setting.builder().type("SUBJECT_CATEGORY").value("Design").order(2)
                    .description("Design subjects").status(SettingStatus.ACTIVE).build();
            Setting s3 = Setting.builder().type("POST_TYPE").value("News").order(1)
                    .description("News posts").status(SettingStatus.ACTIVE).build();
            settingRepository.save(s1);
            settingRepository.save(s2);
            settingRepository.save(s3);
        }
    }
}
