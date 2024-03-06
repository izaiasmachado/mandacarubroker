package com.mandacarubroker.controller;

import com.mandacarubroker.domain.auth.RequestAuthUserDTO;
import com.mandacarubroker.domain.auth.RequestUserRegisterDTO;
import com.mandacarubroker.domain.auth.ResponseAuthUserDTO;
import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.service.AuthService;
import com.mandacarubroker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Optional;

@Tag(name = "Autenticação", description = "Operações relacionadas a autenticação do usuário")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(final AuthService receivedAuthService, final UserService receivedUserService) {
        this.authService = receivedAuthService;
        this.userService = receivedUserService;
    }

    @Operation(summary = "Login do usuário", description = "Realiza o login do usuário e retorna o token de autenticação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem sucedido"),
            @ApiResponse(responseCode = "401", description = "Usuário ou senha inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseAuthUserDTO> login(@Valid @RequestBody final RequestAuthUserDTO requestAuthUserDTO) {
        Optional<ResponseAuthUserDTO> responseAuthUserDTO = authService.login(requestAuthUserDTO);

        if (responseAuthUserDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(responseAuthUserDTO.orElseThrow());
    }

    @Operation(summary = "Cria um usuário", description = "Cria um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado"),
            @ApiResponse(responseCode = "409", description = "Nome de usuário (username) já usado")
    })
    @PostMapping("/register")
    public ResponseEntity<ResponseUserDTO> register(@Valid @RequestBody final RequestUserRegisterDTO requestAuthUserDTO) {
        Optional<ResponseUserDTO> responseAuthUserDTO = authService.register(requestAuthUserDTO);

        if (responseAuthUserDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        ResponseUserDTO responseUserDTO = responseAuthUserDTO.orElseThrow();
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUserDTO);
    }
}
