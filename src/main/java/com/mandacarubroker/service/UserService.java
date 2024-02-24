package com.mandacarubroker.service;

import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import static com.mandacarubroker.validation.RecordValidation.validateRequestDTO;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(final UserRepository recievedUserRepository) {
        this.userRepository = recievedUserRepository;
    }

    public User createUser(final RequestUserDTO requestUserDTO) {
        validateRequestDTO(requestUserDTO);
        User newUser = new User(requestUserDTO);
        return userRepository.save(newUser);
    }
}
