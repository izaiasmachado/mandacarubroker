package com.mandacarubroker.service;

import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.mandacarubroker.validation.RecordValidation.validateRequestDTO;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService = new PasswordHashingService();

    public UserService(final UserRepository recievedUserRepository) {
        this.userRepository = recievedUserRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(final String userId) {
        return userRepository.findById(userId);
    }

    private User hashPassword(final User user) {
        final String rawPassword = user.getPassword();
        final String hashedPassword = passwordHashingService.hashPassword(rawPassword);
        user.setPassword(hashedPassword);
        return user;
    }

    public User createUser(final RequestUserDTO requestUserDTO) {
        validateRequestDTO(requestUserDTO);
        User newUser = new User(requestUserDTO);
        User hashedPasswordUser = hashPassword(newUser);
        return userRepository.save(hashedPasswordUser);
    }

    public Optional<User> updateUser(final String userId, final RequestUserDTO requestUserDTO) {
        validateRequestDTO(requestUserDTO);

        final String rawPassword = requestUserDTO.password();
        final String hashedPassword = passwordHashingService.hashPassword(rawPassword);

        return userRepository.findById(userId)
                .map(user -> {
                    user.setEmail(requestUserDTO.email());
                    user.setUsername(requestUserDTO.username());
                    user.setPassword(requestUserDTO.password());
                    user.setFirstName(requestUserDTO.firstName());
                    user.setLastName(requestUserDTO.lastName());
                    user.setBirthDate(requestUserDTO.birthDate());
                    user.setBalance(requestUserDTO.balance());
                    user.setPassword(hashedPassword);
                    return userRepository.save(user);
                });
    }

    public void deleteUser(final String id) {
        userRepository.deleteById(id);
    }
}
