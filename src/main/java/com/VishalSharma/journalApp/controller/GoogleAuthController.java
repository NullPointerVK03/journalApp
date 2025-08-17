package com.VishalSharma.journalApp.controller;

import com.VishalSharma.journalApp.services.GoogleOAuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth/google")
@Tag(name = "Auth by Google", description = "Allows OAuth2 using Google")
@SecurityRequirements(value = {}) // Public endpoint (no JWT required)
public class GoogleAuthController {

    private final GoogleOAuthService googleOAuthService;

    public GoogleAuthController(GoogleOAuthService googleOAuthService) {
        this.googleOAuthService = googleOAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<String> googleOAuthHandler(@RequestParam String authCode) {
        log.info("Incoming GET /auth/google/callback with authCode");

        try {
            String jwtToken = googleOAuthService.authenticate(authCode);

            if (jwtToken != null) {
                log.info("Google OAuth authentication successful. JWT generated.");
                return ResponseEntity.ok(jwtToken);
            } else {
                log.warn("Google OAuth authentication failed. Invalid authCode provided.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication failed: Invalid auth code.");
            }

        } catch (Exception e) {
            log.error("Error during Google OAuth authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Authentication failed due to server error.");
        }
    }
}
