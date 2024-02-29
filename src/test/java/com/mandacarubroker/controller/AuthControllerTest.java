package com.mandacarubroker.controller;

import com.mandacarubroker.domain.auth.RequestAuthUserDTO;
import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.service.AuthService;
import com.mandacarubroker.service.PasswordHashingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthControllerTest {
    @MockBean
    private AuthService authService;
    private AuthController authController;

    private final PasswordHashingService passwordHashingService = new PasswordHashingService();

    private final String validEmail = "lara.souza@gmail.com";
    private final String validUsername = "username";
    private final String invalidUsername = "invalidUsername";
    private final String validPassword = "password";
    private final String invalidPassword = "invalidPassword";
    private final String validHashedPassword = passwordHashingService.hashPassword(validPassword);
    private final String validFirstName = "Lara";
    private final String validLastName = "Souza";
    private final LocalDate validBirthDate = LocalDate.of(1997,4,5);
    private final double validBalance = 90.50;

    private final RequestUserDTO validRequestUserDTO = new RequestUserDTO(
            validEmail,
            validUsername,
            validHashedPassword,
            validFirstName,
            validLastName,
            validBirthDate,
            validBalance
    );

    private final RequestAuthUserDTO validRequestAuthUserDTO = new RequestAuthUserDTO(
            validUsername,
            validPassword
    );

    @BeforeEach
    void setUp() {
        authService = Mockito.mock(AuthService.class);
        User validUser = new User(validRequestUserDTO);
        Optional<User> optionalValidUser = Optional.of(validUser);
        Mockito.when(authService.login(validRequestAuthUserDTO)).thenReturn(optionalValidUser);
        Mockito.when(authService.login(new RequestAuthUserDTO(invalidUsername, validPassword))).thenReturn(Optional.empty());
        Mockito.when(authService.login(new RequestAuthUserDTO(validUsername, invalidPassword))).thenReturn(Optional.empty());
        authController = new AuthController(authService);
    }

    @Test
    void itShouldBeAbleToLoginWithValidUser() {
        ResponseEntity<String> response = authController.login(validRequestAuthUserDTO);
        assertEquals(ResponseEntity.ok("User logged in successfully"), response);
    }

    @Test
    void itShouldNotBeAbleToLoginWithInvalidUser() {
        RequestAuthUserDTO invalidRequestAuthUserDTO = new RequestAuthUserDTO(
                invalidUsername,
                validPassword
        );

        ResponseEntity<String> response = authController.login(invalidRequestAuthUserDTO);
        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), response);
    }

    @Test
    void itShouldNotBeAbleToLoginWithInvalidPassword() {
        RequestAuthUserDTO invalidRequestAuthUserDTO = new RequestAuthUserDTO(
                validUsername,
                invalidPassword
        );

        ResponseEntity<String> response = authController.login(invalidRequestAuthUserDTO);
        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), response);
    }
}
