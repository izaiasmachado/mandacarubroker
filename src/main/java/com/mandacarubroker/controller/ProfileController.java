package com.mandacarubroker.controller;
import com.mandacarubroker.domain.profile.RequestProfileDTO;
import com.mandacarubroker.domain.profile.ResponseProfileDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.service.AuthService;
import com.mandacarubroker.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Tag(name = "Perfil do Usuário", description = "Operações relacionadas ao perfil do usuário. User role: user")
@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(final ProfileService receivedProfileService) {
        this.profileService = receivedProfileService;
    }

    @Operation(summary = "Retorna dados de um usuário", description = "Mostra dados de um usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil de usuário encontrado")
    })
    @GetMapping("/me")
    public ResponseEntity<ResponseProfileDTO> getCurrentUser() {
        User user = AuthService.getAuthenticatedUser();
        ResponseProfileDTO responseUserDTO = ResponseProfileDTO.fromUser(user);
        return ResponseEntity.ok(responseUserDTO);
    }

    @Operation(summary = "Atualiza dados cadastrais do usuário", description = "Atualiza dados cadastrais do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/me")
    public ResponseEntity<ResponseProfileDTO> updateUser(@Valid @RequestBody final RequestProfileDTO updatedUserDTO) {
        User user = AuthService.getAuthenticatedUser();
        String userName = user.getUsername();
        Optional<ResponseProfileDTO> updatedProfile = profileService.updateProfile(userName, updatedUserDTO);

        if (updatedProfile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedProfile.get());
    }
}
