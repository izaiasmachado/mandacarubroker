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
}
