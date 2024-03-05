package com.mandacarubroker.controller;
import com.mandacarubroker.domain.profile.RequestProfileDTO;
import com.mandacarubroker.domain.profile.ResponseProfileDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.service.AuthService;
import com.mandacarubroker.service.ProfileService;
import com.mandacarubroker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(final ProfileService receivedProfileService, final UserService receivedUserService) {
        this.profileService = receivedProfileService;
        this.userService = receivedUserService;
    }

    @PutMapping("/me/{userName}")
    public ResponseEntity<Object> updateUser(@PathVariable final String userName, @RequestBody @Valid final RequestProfileDTO updatedUserDTO) {
        User user = AuthService.getAuthenticatedUser();

        Optional<ResponseProfileDTO> updatedProfile = profileService.updateProfile(userName, updatedUserDTO);

        if (updatedProfile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedProfile.get());
    }

}
