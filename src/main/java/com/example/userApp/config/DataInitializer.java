package com.example.userApp.config;

import com.example.userApp.model.User;
import com.example.userApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    
    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Initializing sample data...");
            createSampleUsers();
            log.info("Sample data initialized successfully");
        } else {
            log.info("Database already contains data, skipping initialization");
        }
    }
    
    private void createSampleUsers() {
        User user1 = new User();
        user1.setUsername("johndoe");
        user1.setEmail("john.doe@example.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setPhoneNumber("+1234567890");
        user1.setIsActive(true);
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());
        
        User user2 = new User();
        user2.setUsername("jansmith");
        user2.setEmail("jane.smith@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setPhoneNumber("+9876543210");
        user2.setIsActive(true);
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        
        User user3 = new User();
        user3.setUsername("bobwilson");
        user3.setEmail("bob.wilson@example.com");
        user3.setFirstName("Bob");
        user3.setLastName("Wilson");
        user3.setPhoneNumber("+5555555555");
        user3.setIsActive(false);
        user3.setCreatedAt(LocalDateTime.now());
        user3.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        
        log.info("Created {} sample users", 3);
    }
}
