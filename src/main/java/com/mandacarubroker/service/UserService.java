package com.mandacarubroker.service;

import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mandacarubroker.validation.RecordValidation.validateRequestDTO;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService = new PasswordHashingService();

    public UserService(final UserRepository recievedUserRepository) {
        this.userRepository = recievedUserRepository;
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

    public List<ResponseUserDTO> getAllUsers() {
        List<ResponseUserDTO> allUsers = new ArrayList<>();
        List<User> retrievedUsers = userRepository.findAll();

        for (User retrievedUser : retrievedUsers) {
            allUsers.add(userToResponseUserDTO(retrievedUser));
        }
        return allUsers;
    }

    public Optional<ResponseUserDTO> getUserById(final String userId) {
        Optional<User> retrievedUser = userRepository.findById(userId);
        if (!retrievedUser.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(userToResponseUserDTO(retrievedUser.get()));
    }

    private User hashPassword(final User user) {
        final String rawPassword = user.getPassword();
        final String hashedPassword = passwordHashingService.encode(rawPassword);
        user.setPassword(hashedPassword);
        return user;
    }

    public ResponseUserDTO createUser(final RequestUserDTO requestUserDTO) {
        validateRequestDTO(requestUserDTO);
        User newUser = new User(requestUserDTO);
        User hashedPasswordUser = hashPassword(newUser);
        return userToResponseUserDTO(userRepository.save(hashedPasswordUser));
    }

    public Optional<ResponseUserDTO> updateUser(final String userId, final RequestUserDTO requestUserDTO) {
        validateRequestDTO(requestUserDTO);

        final String rawPassword = requestUserDTO.password();
        final String hashedPassword = passwordHashingService.encode(rawPassword);

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
                    return userToResponseUserDTO(userRepository.save(user));
                });
    }

    public void deleteUser(final String id) {
        userRepository.deleteById(id);
    }

    public UserDetails loadUserByUsername(final String username) {
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Username is required");
        }

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public boolean verifyDuplicateUsername(final String userName) {
        User alreadyExistingUser = userRepository.findByUsername(userName);
        return alreadyExistingUser != null;
    }
}
