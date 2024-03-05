package com.mandacarubroker.controller;
import com.mandacarubroker.domain.profile.RequestProfileDTO;
import com.mandacarubroker.domain.profile.ResponseProfileDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.service.AuthService;
import com.mandacarubroker.service.ProfileService;
import com.mandacarubroker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.puppycrawl.tools.checkstyle.grammar.javadoc.JavadocLexer.exception;
import static net.sf.saxon.om.EnumSetTool.except;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(final ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
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
