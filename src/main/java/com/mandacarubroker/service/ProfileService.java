package com.mandacarubroker.service;

import com.mandacarubroker.domain.profile.RequestProfileDTO;
import com.mandacarubroker.domain.profile.ResponseProfileDTO;
import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mandacarubroker.validation.RecordValidation.validateRequestDTO;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService = new PasswordHashingService();

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private ResponseProfileDTO profileToResponseUserDTO(final User user) {
        return new ResponseProfileDTO(
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate()
        );
    }

    private ResponseUserDTO userToResponseUserDTO(final User user) {
        return new ResponseUserDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getBalance()
        );
    }

    public Optional<ResponseUserDTO> findByUsername(final String username) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        return user.map(this::userToResponseUserDTO);
    }

    public Optional<ResponseProfileDTO> updateProfile(final String userName, final RequestProfileDTO requestProfileDTO) {
        validateRequestDTO(requestProfileDTO);

        final String rawPassword = requestProfileDTO.password();
        final String hashedPassword = passwordHashingService.encode(rawPassword);
        String userId = findByUsername(userName).get().id();

        return userRepository.findById(userId)
                .map(user -> {
                    user.setEmail(requestProfileDTO.email());
                    user.setUsername(requestProfileDTO.username());
                    user.setPassword(requestProfileDTO.password());
                    user.setFirstName(requestProfileDTO.firstName());
                    user.setLastName(requestProfileDTO.lastName());
                    user.setBirthDate(requestProfileDTO.birthDate());
                    user.setPassword(hashedPassword);
                    return profileToResponseUserDTO(userRepository.save(user));
                });
    }
}