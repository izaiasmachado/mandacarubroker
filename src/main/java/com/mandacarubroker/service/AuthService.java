package com.mandacarubroker.service;

import com.mandacarubroker.domain.auth.RequestAuthUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.mandacarubroker.validation.RecordValidation.validateRequestDTO;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService = new PasswordHashingService();

    public AuthService(final UserRepository receivedUserRepository) {
        this.userRepository = receivedUserRepository;
    }

    public Optional<User> login(final RequestAuthUserDTO requestAuthUserDTO) {
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
}
