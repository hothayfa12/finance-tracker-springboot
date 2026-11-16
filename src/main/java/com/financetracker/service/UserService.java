
package com.financetracker.service;

import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.model.User;
import com.financetracker.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(
            String firstName,
            String lastName,
            String email,
            String password) {

        if (firstName == null || firstName.isBlank()
                || lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException(
                    "First name and last name are required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required");
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters");
        }

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        User user = new User();

        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(
                passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Integer id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required");
        }

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
    }
}
