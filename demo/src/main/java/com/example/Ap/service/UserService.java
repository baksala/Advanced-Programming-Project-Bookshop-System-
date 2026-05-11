package com.example.Ap.service;

import com.example.Ap.dto.RegisterRequest;
import com.example.Ap.model.User;
import com.example.Ap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // bonus: Email + Multithreading

    @Transactional
    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("An account with that email already exists.");
        }
        if (req.isPasswordsMissmMatch()) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        String[] parts = req.getFullName().trim().split("\\s+", 2);
        String first = parts[0];
        String last  = parts.length > 1 ? parts[1] : "";

        User user = User.builder()
                .firstName(first)
                .lastName(last)
                .email(req.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(req.getPassword()))
                .newsletter(req.isNewsletter())
                .role(User.Role.USER)
                .build();

        User saved = userRepository.save(user);

        // Bonus: Send welcome email asynchronously (Multithreading via @Async)
        emailService.sendWelcomeEmail(saved.getEmail(), saved.getFirstName());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    @Transactional
    public void updateProfile(User user, String firstName, String lastName,
                              String email, String bio) {
        if (!user.getEmail().equalsIgnoreCase(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("That email is already in use.");
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email.toLowerCase().trim());
        user.setBio(bio);
    }

    @Transactional
    public void changePassword(User user, String currentRaw, String newRaw) {
        if (!passwordEncoder.matches(currentRaw, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        user.setPassword(passwordEncoder.encode(newRaw));
        userRepository.save(user);
    }
}
