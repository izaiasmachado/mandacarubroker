package com.mandacarubroker.controller;


import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(final UserService receivedUserService) {
        this.userService = receivedUserService;
    }

    @GetMapping
    public List<ResponseUserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUserDTO> getUserById(@PathVariable final String id) {
        Optional<ResponseUserDTO> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.get());
    }

    @PostMapping
    public ResponseEntity<ResponseUserDTO> createUser(@RequestBody @Valid final RequestUserDTO requestUserDTO) {
        if (userService.verifyDuplicateUsername(requestUserDTO.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        ResponseUserDTO createdUser = userService.createUser(requestUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseUserDTO> updateUser(@PathVariable final String id, @RequestBody @Valid final RequestUserDTO updatedUserDTO) {
        Optional<ResponseUserDTO> updatedUser = userService.updateUser(id, updatedUserDTO);

        if (updatedUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedUser.get());
    }

    @GetMapping("/deposit")
    public ResponseEntity<ResponseUserDTO> deposit(@RequestParam @Positive final double amount) {
        ResponseUserDTO user = userService.doDepositForAuthenticatedUser(amount);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/withdraw")
    public ResponseEntity<ResponseUserDTO> withdraw(@RequestParam @Positive final double amount) {
        ResponseUserDTO user = userService.doWithdrawForAuthenticatedUser(amount);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable final String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

