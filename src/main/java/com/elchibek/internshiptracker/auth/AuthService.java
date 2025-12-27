package com.elchibek.internshiptracker.auth;

import com.elchibek.internshiptracker.security.JwtService;
import com.elchibek.internshiptracker.user.User;
import com.elchibek.internshiptracker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(RegisterRequest req) {
        String email = normalizeEmail(req.email());

        if (userRepository.existsByEmail(email)) {
            // You can later replace with a custom exception and 409 Conflict
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(req.password()));

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest req) {
        String email = normalizeEmail(req.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
