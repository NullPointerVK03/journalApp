package com.VishalSharma.journalApp.controller;

import com.VishalSharma.journalApp.dto.UserDTO;
import com.VishalSharma.journalApp.services.UserDetailsServiceImpl;
import com.VishalSharma.journalApp.services.UserService;
import com.VishalSharma.journalApp.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/dashboard")
    @Operation(description = "Public dashboard")
    public ResponseEntity<String> dashboard() {
        try {
            log.info("Incoming GET request to access Public dashboard");

            String msg = "Welcome! public dashboard is working fine.";
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Something went wrong while accessing public dashboard. Exception: ", e);
            return new ResponseEntity<>("Something went wrong with public dashboard", HttpStatus.BAD_REQUEST);
        }
    }

    //    CRUD operations
    //    creating new user
    @PostMapping("/signup")
    @Operation(description = "Signup")
    public ResponseEntity<Void> signup(@RequestBody UserDTO user) {
        try {
            log.info("Incoming POST request to create a new user");
            userService.createNewUser(user);

            log.info("User with userName: {} created", user.getUserName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.warn("Something went wrong while creating a newUser with userName: {}", user.getUserName());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    @Operation(description = "Login")
    public ResponseEntity<String> login(@RequestBody UserDTO user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            log.info("Incoming POST request to login with userName: {}", user.getUserName());

            log.info("Getting UserDetails for userName: {}", user.getUserName());
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());

            log.info("Generating JWT token for userName: {}", user.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            log.info("JWT token generated for userName: {}", user.getUserName());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Exception occurred while creating JWT token for userName: {}. Exception: ", user.getUserName(), e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }
}


