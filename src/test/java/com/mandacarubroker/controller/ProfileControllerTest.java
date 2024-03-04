package com.mandacarubroker.controller;

import com.mandacarubroker.domain.auth.RequestAuthUserDTO;
import com.mandacarubroker.domain.auth.ResponseAuthUserDTO;
import com.mandacarubroker.domain.auth.RequestAuthUserDTO;
import com.mandacarubroker.domain.profile.RequestProfileDTO;
import com.mandacarubroker.domain.profile.ResponseProfileDTO;
import com.mandacarubroker.domain.user.*;
import com.mandacarubroker.security.SecuritySecretsMock;
import com.mandacarubroker.service.AuthService;
import com.mandacarubroker.service.ProfileService;
import com.mandacarubroker.service.PasswordHashingService;
import com.mandacarubroker.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import com.mandacarubroker.domain.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static com.mandacarubroker.domain.user.Permission.*;
import static com.mandacarubroker.domain.user.Permission.USER_DELETE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ProfileControllerTest {
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private ProfileService profileService;

    private AuthService authService;
    private UserService userService;
    private AuthController authController;
    private ProfileController profileController;
    private static final String TOKEN_TYPE = "Bearer";
    private final PasswordHashingService passwordHashingService = new PasswordHashingService();
    private final String validEmail = "lara.souza@gmail.com";
    private final String validUsername = "username";
    private final String invalidUsername = "invalidUsername";
    private final String validPassword = "password";
    private final String invalidPassword = "invalidPassword";
    private final String validHashedPassword = passwordHashingService.encode(validPassword);
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

    private final ResponseAuthUserDTO validResponseAuthUserDTO = new ResponseAuthUserDTO(
            "Bearer token",
            86400,
            "Bearer"
    );
    private final RequestProfileDTO validRequestProfileDTO = new RequestProfileDTO(
            validEmail,
            validUsername,
            validPassword,
            validFirstName,
            validLastName,
            validBirthDate
    );
    private final ResponseProfileDTO validResponseProfileDTO = new ResponseProfileDTO(
            validEmail,
            validUsername,
            validPassword,
            validFirstName,
            validLastName,
            validBirthDate
    );

    @BeforeEach
    void setUp() {
        profileService = Mockito.mock(ProfileService.class);
        userService = Mockito.mock(UserService.class);

        Mockito.when(profileService.updateProfile(validUsername, validRequestProfileDTO)).thenReturn(Optional.of(validResponseProfileDTO));
        profileController = new ProfileController(profileService, userService);

        Collection<SimpleGrantedAuthority> authorities = Set.of(
                new SimpleGrantedAuthority(USER_CREATE.getPermission()),
                new SimpleGrantedAuthority(USER_READ.getPermission()),
                new SimpleGrantedAuthority(USER_UPDATE.getPermission()),
                new SimpleGrantedAuthority(USER_DELETE.getPermission()));
        UserDetails user = userService.loadUserByUsername(validUsername);
        Authentication authentication = new TestingAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecuritySecretsMock.mockStatic();
    }

    @Test
    void itShouldReturnOkWhenUpdatingProfile() {
        ResponseEntity<Object> resposnse = profileController.updateUser(validRequestProfileDTO);
        assertEquals(ResponseEntity.status(HttpStatus.OK).build(), resposnse);
    }
}
