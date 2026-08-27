package vn.edu.crs.auth_service.config;

import vn.edu.crs.auth_service.entity.User;
import vn.edu.crs.auth_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.admin-password}")
    private String adminPassword;

    @Value("${seed.student-password}")
    private String studentPassword;

    @Override
    public void run(String... args) {

        if (userRepository.findByUsername("admin").isEmpty()) {

            User admin = new User();

            admin.setUsername("admin");
            admin.setPassword(
                    passwordEncoder.encode(adminPassword)
            );
            admin.setRole("ADMIN");

            userRepository.save(admin);
        }

        if (userRepository.findByUsername("student1").isEmpty()) {

            User student = new User();

            student.setUsername("student1");
            student.setPassword(
                    passwordEncoder.encode(studentPassword)
            );
            student.setRole("STUDENT");

            userRepository.save(student);
        }
    }
}