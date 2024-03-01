package com.mandacarubroker.controller;

import com.mandacarubroker.domain.auth.RequestAuthUserDTO;
import com.mandacarubroker.domain.auth.ResponseAuthUserDTO;
import com.mandacarubroker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(final AuthService receivedAuthService) {
        this.authService = receivedAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseAuthUserDTO> login(@Valid @RequestBody final RequestAuthUserDTO requestAuthUserDTO) {
        Optional<ResponseAuthUserDTO> responseAuthUserDTO = authService.login(requestAuthUserDTO);

        if (responseAuthUserDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(responseAuthUserDTO.orElseThrow());
    }
}
