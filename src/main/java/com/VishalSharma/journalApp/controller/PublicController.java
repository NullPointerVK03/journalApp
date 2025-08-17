package com.VishalSharma.journalApp.controller;

import com.VishalSharma.journalApp.dto.UserDTO;
import com.VishalSharma.journalApp.services.UserDetailsServiceImpl;
import com.VishalSharma.journalApp.services.UserService;
import com.VishalSharma.journalApp.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
@Tag(name = "Public APIs", description = "Signup, login")
@SecurityRequirements(value = {})
public class PublicController {

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public PublicController(UserService userService,
                            UserDetailsServiceImpl userDetailsService,
                            AuthenticationManager authenticationManager,
                            JwtUtil jwtUtil) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/dashboard")
    @Operation(description = "Public dashboard")
    public ResponseEntity<String> dashboard() {
        try {
            log.info("GET /public/dashboard invoked");

            String msg = "Welcome! Public dashboard is working fine.";
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            log.error("Error accessing /public/dashboard", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong with public dashboard");
        }
    }

    @PostMapping("/signup")
    @Operation(description = "Signup")
    public ResponseEntity<String> signup(@RequestBody UserDTO user) {
        try {
            log.info("POST /public/signup invoked to create user: {}", user.getUserName());

            userService.createNewUser(user);

            log.info("User created successfully: {}", user.getUserName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User created successfully.");
        } catch (Exception e) {
            log.error("Error creating user: {}", user.getUserName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while creating a new user");
        }
    }

    @PostMapping("/login")
    @Operation(description = "Login")
    public ResponseEntity<String> login(@RequestBody UserDTO user) {
        try {
            log.info("POST /public/login attempt for user: {}", user.getUserName());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
            );

            log.info("Authentication successful for user: {}", user.getUserName());

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());

            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            log.info("JWT generated successfully for user: {}", user.getUserName());
            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            log.warn("Invalid login attempt for user: {}", user.getUserName(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password");
        }
    }
}
