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

    public UserService(final UserRepository recievedUserRepository) {
        this.userRepository = recievedUserRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(final String userId) {
        return userRepository.findById(userId);
    }

    public User createUser(final RequestUserDTO requestUserDTO) {
        validateRequestDTO(requestUserDTO);
        User newUser = new User(requestUserDTO);
        return userRepository.save(newUser);
    }

    public Optional<User> updateUser(final String userId, final RequestUserDTO requestUserDTO) {
        validateRequestDTO(requestUserDTO);
        return userRepository.findById(userId)
                .map(user -> {
                    user.setEmail(requestUserDTO.email());
                    user.setUsername(requestUserDTO.username());
                    user.setPassword(requestUserDTO.password());
                    user.setFirstName(requestUserDTO.firstName());
                    user.setLastName(requestUserDTO.lastName());
                    user.setBirthDate(requestUserDTO.birthDate());
                    user.setBalance(requestUserDTO.balance());
                    return userRepository.save(user);
                });
    }

    public Optional<User> deposit(final String userId, final double amount) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return Optional.empty();
        }
        User user = userOptional.get();
        user.deposit(amount);
        return Optional.of(userRepository.save(user));
    }

    public void deleteUser(final String id) {
        userRepository.deleteById(id);
    }
}
