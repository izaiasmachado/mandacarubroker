package com.mandacarubroker.service;

import com.mandacarubroker.domain.auth.RequestAuthUserDTO;
import com.mandacarubroker.domain.auth.ResponseAuthUserDTO;
import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import com.mandacarubroker.security.SecuritySecretsMock;
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
    private PasswordHashingService passwordHashingService;
    private TokenService tokenService;

    private final String validEmail = "lara.souza@gmail.com";
    private final String validUsername = "username";
    private final String invalidUsername = "invalidUsername";
    private final String validPassword = "password";
    private final String invalidPassword = "invalidPassword";
    private final String validHashedPassword = "hashedPassword";
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

    private final String validToken = "Bearer token";
    private final int expiresIn = 86400;
    private final String tokenType = "Bearer";


    private User validUser;

    @BeforeEach
    void setUp() {
        SecuritySecretsMock.mockStatic();

        userRepository = Mockito.mock(UserRepository.class);
        validUser = new User(validRequestUserDTO);
        Mockito.when(userRepository.findByUsername(validUsername)).thenReturn(validUser);
        Mockito.when(userRepository.findByUsername(invalidUsername)).thenReturn(null);

        passwordHashingService = Mockito.mock(PasswordHashingService.class);
        Mockito.when(passwordHashingService.matches(validPassword, validHashedPassword)).thenReturn(true);
        Mockito.when(passwordHashingService.matches(invalidPassword, validHashedPassword)).thenReturn(false);

        tokenService = Mockito.mock(TokenService.class);
        Mockito.when(tokenService.encodeToken(validUser.getUsername())).thenReturn(new ResponseAuthUserDTO(validToken, expiresIn, tokenType));

        authService = new AuthService(userRepository, passwordHashingService, tokenService);
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
    void itShouldBeAbleGetUserGivenValidCredentials() {
        Optional<User> user = authService.getUserGivenCredentials(validRequestAuthUserDTO);
        assertEquals(true, user.isPresent());
        assertUsersAreEqual(validUser, user.get());
    }

    @Test
    void itShouldNotBeAbleToGetUserGivenInvalidPassword() {
        RequestAuthUserDTO invalidRequestAuthUserDTO = new RequestAuthUserDTO(
                validUsername,
                invalidPassword
        );
        Optional<User> user = authService.getUserGivenCredentials(invalidRequestAuthUserDTO);
        assertEquals(false, user.isPresent());
    }

    @Test
    void itShouldNotBeAbleToGetUserGivenInvalidUsername() {
        RequestAuthUserDTO invalidRequestAuthUserDTO = new RequestAuthUserDTO(
                invalidUsername,
                validPassword
        );
        Optional<User> user = authService.getUserGivenCredentials(invalidRequestAuthUserDTO);
        assertEquals(false, user.isPresent());
    }

    @Test
    void itShouldBeAbleToGetJwtTokenGivenValidUser() {
        Optional<ResponseAuthUserDTO> responseAuthUserDTO = authService.login(validRequestAuthUserDTO);
        assertEquals(true, responseAuthUserDTO.isPresent());
        assertEquals(validToken, responseAuthUserDTO.get().token());
        assertEquals(expiresIn, responseAuthUserDTO.get().expiresIn());
        assertEquals(tokenType, responseAuthUserDTO.get().tokenType());
    }

    @Test
    void itShouldNotBeAbleToGetJwtTokenGivenInvalidUsername() {
        RequestAuthUserDTO invalidRequestAuthUserDTO = new RequestAuthUserDTO(
                invalidUsername,
                validPassword
        );
        Optional<ResponseAuthUserDTO> responseAuthUserDTO = authService.login(invalidRequestAuthUserDTO);
        assertEquals(false, responseAuthUserDTO.isPresent());
    }

    @Test
    void itShouldNotBeAbleToGetJwtTokenGivenInvalidPassword() {
        RequestAuthUserDTO invalidRequestAuthUserDTO = new RequestAuthUserDTO(
                validUsername,
                invalidPassword
        );
        Optional<ResponseAuthUserDTO> responseAuthUserDTO = authService.login(invalidRequestAuthUserDTO);
        assertEquals(false, responseAuthUserDTO.isPresent());
    }
}
