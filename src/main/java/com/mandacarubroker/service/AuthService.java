package com.mandacarubroker.service;

import com.mandacarubroker.domain.auth.RequestAuthUserDTO;
import com.mandacarubroker.domain.auth.ResponseAuthUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.mandacarubroker.validation.RecordValidation.validateRequestDTO;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordHashingService passwordHashingService;
    private final TokenService tokenService;

    public AuthService(final UserRepository receivedUserRepostory, final PasswordHashingService receivedPasswordHashingService, final TokenService receivedTokenService) {
        this.userRepository = receivedUserRepostory;
        this.passwordHashingService = receivedPasswordHashingService;
        this.tokenService = receivedTokenService;
    }

    public Optional<ResponseAuthUserDTO> login(final RequestAuthUserDTO requestAuthUserDTO) {
        Optional<User> user = getUserGivenCredentials(requestAuthUserDTO);

        if (user.isEmpty()) {
            return Optional.empty();
        }

        String userId = user.get().getUsername();
        ResponseAuthUserDTO responseAuthUserDTO = tokenService.encodeToken(userId);
        return Optional.of(responseAuthUserDTO);
    }

    public Optional<User> getUserGivenCredentials(final RequestAuthUserDTO requestAuthUserDTO) {
        validateRequestDTO(requestAuthUserDTO);

        User user = userRepository.findByUsername(requestAuthUserDTO.username());

        if (user == null) {
            return Optional.empty();
        }

        final String givenPassword = requestAuthUserDTO.password();
        final String storedPassword = user.getPassword();
        final boolean isPasswordCorrect = passwordHashingService.matches(givenPassword, storedPassword);

        if (!isPasswordCorrect) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    public static User getAuthenticatedUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        return (User) principal;
    }
}
