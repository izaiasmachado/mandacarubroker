package com.mandacarubroker.service;

import com.mandacarubroker.domain.auth.RequestAuthUserDTO;
import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthServiceTest {
    @MockBean
    private UserRepository userRepository;
    private AuthService authService;
    private final PasswordHashingService passwordHashingService = new PasswordHashingService();

    private final String validEmail = "lara.souza@gmail.com";
    private final String validUsername = "username";
    private final String invalidUsername = "invalidUsername";
    private final String validPassword = "password";
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

    private User validUser;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        validUser = new User(validRequestUserDTO);
        Mockito.when(userRepository.findByUsername(validUsername)).thenReturn(validUser);
        Mockito.when(userRepository.findByUsername(invalidUsername)).thenReturn(null);
        authService = new AuthService(userRepository);
    }

    private void assertUsersAreEqual(final User expected, final User actual) {
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getBirthDate(), actual.getBirthDate());
        assertEquals(expected.getBalance(), actual.getBalance());
    }

    @Test
    void itShouldBeAbleToLoginWithValidUser() {
        RequestAuthUserDTO validRequestAuthUserDTO = new RequestAuthUserDTO(
                validUsername,
                validPassword
        );

        Optional<User> user = authService.login(validRequestAuthUserDTO);
        assertEquals(true, user.isPresent());
        assertUsersAreEqual(validUser, user.get());
    }

    @Test
    void itShouldNotBeAbleToLoginWithInvalidPassword() {
        RequestAuthUserDTO invalidRequestAuthUserDTO = new RequestAuthUserDTO(
                validUsername,
                "invalidPassword"
        );
        Optional<User> user = authService.login(invalidRequestAuthUserDTO);
        assertEquals(false, user.isPresent());
    }

    @Test
    void itShouldNotBeAbleToLoginWithInvalidUsername() {
        RequestAuthUserDTO invalidRequestAuthUserDTO = new RequestAuthUserDTO(
                invalidUsername,
                validPassword
        );
        Optional<User> user = authService.login(invalidRequestAuthUserDTO);
        assertEquals(false, user.isPresent());
    }
}