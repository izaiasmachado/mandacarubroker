package com.mandacarubroker.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashingServiceTest {

    @Test
    void itShouldBeAbleToHashAndMatchRightPassword() {
        final PasswordHashingService underTest = new PasswordHashingService();
        final String hashedPassword = underTest.hashPassword("password");
        final boolean matches = underTest.matches("password", hashedPassword);
        assertTrue(matches);
    }

    @Test
    void itShouldNotMatchWrongPassword() {
        final PasswordHashingService underTest = new PasswordHashingService();
        final String hashedPassword = underTest.hashPassword("password");
        final boolean matches = underTest.matches("wrongPassword", hashedPassword);
        assertFalse(matches);
    }
}