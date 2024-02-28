package com.mandacarubroker.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordHashingService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hashPassword(final String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public boolean matches(final String plainPassword, final String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
