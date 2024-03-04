package com.mandacarubroker.controller;
import com.mandacarubroker.domain.profile.RequestProfileDTO;
import com.mandacarubroker.domain.profile.ResponseProfileDTO;
import com.mandacarubroker.service.ProfileService;
import com.mandacarubroker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PutMapping("/me")
    public ResponseEntity<Object> updateUser(@RequestBody @Valid final RequestProfileDTO updatedUserDTO) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String userName = userDetails.getUsername();
            Optional<ResponseProfileDTO> updatedProfile = profileService.updateProfile(userName, updatedUserDTO);
            return ResponseEntity.ok(updatedProfile.get());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
