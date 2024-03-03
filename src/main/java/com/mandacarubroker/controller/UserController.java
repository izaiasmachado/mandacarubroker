package com.mandacarubroker.controller;


import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Optional;

@Tag(name = "Usuário", description = "Operações de CRUD do usuário")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(final UserService receivedUserService) {
        this.userService = receivedUserService;
    }

    @Operation(summary = "Retorna todos os usuários", description = "Retorna uma lista com todos os usuários cadastrados")
    @GetMapping
    public List<ResponseUserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Retorna um usuário", description = "Retorna um usuário cadastrado com base no id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseUserDTO> getUserById(@PathVariable final String id) {
        Optional<ResponseUserDTO> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.get());
    }

    @Operation(summary = "Cria um usuário", description = "Cria um novo usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado"),
        @ApiResponse(responseCode = "409", description = "Nome de usuário (username) já usado")
    })
    @PostMapping
    public ResponseEntity<ResponseUserDTO> createUser(@RequestBody @Valid final RequestUserDTO requestUserDTO) {
        if (userService.verifyDuplicateUsername(requestUserDTO.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        ResponseUserDTO createdUser = userService.createUser(requestUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Atualiza um usuário", description = "Atualiza um usuário cadastrado com base no id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
        @ApiResponse(responseCode = "400", description = "Dados do usuário são inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseUserDTO> updateUser(@PathVariable final String id, @RequestBody @Valid final RequestUserDTO updatedUserDTO) {
        Optional<ResponseUserDTO> updatedUser = userService.updateUser(id, updatedUserDTO);

        if (updatedUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedUser.get());
    }

    @Operation(summary = "Deleta um usuário", description = "Deleta um usuário cadastrado com base no id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable final String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

