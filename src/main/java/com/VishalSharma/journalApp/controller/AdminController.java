package com.VishalSharma.journalApp.controller;


import com.VishalSharma.journalApp.appCache.AppCache;
import com.VishalSharma.journalApp.dto.UserDTO;
import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.repository.UserRepository;
import com.VishalSharma.journalApp.repository.UserRepositoryImpl;
import com.VishalSharma.journalApp.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "ADMIN APIs", description = "ADMIN of the application")
public class AdminController {


    private final UserRepository userRepository;
    private final UserService userService;
    private final AppCache appCache;
    private final UserRepositoryImpl userRepoImpl;

    public AdminController(UserRepository userRepository, UserService userService, AppCache appCache, UserRepositoryImpl userRepoImpl) {
        this.appCache = appCache;
        this.userRepoImpl = userRepoImpl;
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @GetMapping("/dashboard")
    @Operation(description = "Admin dashboard")
    public ResponseEntity<String> dashboard() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminName = authentication.getName();

            log.info("GET /admin/dashboard requested by admin: {}", adminName);
            String msg = "Welcome " + adminName + "! Admin dashboard is working fine.";

            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            log.error("Error accessing /admin/dashboard", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong");
        }
    }

    @GetMapping("/all-users")
    @Operation(description = "Fetches all users from DB")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminName = authentication.getName();

            log.info("GET /admin/all-users requested by admin: {}", adminName);

            List<User> allUsers = userRepository.findAll();

            if (!allUsers.isEmpty()) {
                log.info("Retrieved {} users from DB", allUsers.size());
                return ResponseEntity.ok(allUsers);
            }
            log.warn("No users found in DB");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("Error fetching all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create-new-admin")
    @Operation(description = "Creates a new ADMIN")
    public ResponseEntity<String> createNewAdmin(@RequestBody UserDTO user) {
        try {
            log.info("POST /admin/create-new-admin with username: {}", user.getUserName());

            userService.createNewAdmin(user);

            log.info("New ADMIN created with username: {}", user.getUserName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Admin created successfully.");
        } catch (Exception e) {
            log.error("Error creating new admin with username: {}", user.getUserName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while creating a new ADMIN.");
        }
    }

    @PatchMapping("/grant-as-admin/{userId}")
    @Operation(description = "Grants ADMIN authority to a existing user")
    public ResponseEntity<String> grantAsAdmin(@PathVariable String userId) {
        try {
            log.info("PATCH /admin/grant-as-admin/{} invoked", userId);

            ObjectId id = new ObjectId(userId);
            userService.grantAdminAuthority(id);

            log.info("Granted ADMIN authority to user with ID: {}", userId);
            return ResponseEntity.ok("Admin authority granted successfully.");
        } catch (Exception e) {
            log.warn("Error granting ADMIN authority to userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with id: " + userId);
        }
    }

    @GetMapping("/clear-app-cache")
    @Operation(description = "Clears previous app-cache and loaded latest app cache values")
    public ResponseEntity<String> clearAppCache() {
        try {
            log.info("GET /admin/clear-app-cache invoked");

            appCache.init();

            log.info("Application cache cleared successfully");
            return ResponseEntity.ok("App cache cleared successfully.");
        } catch (Exception e) {
            log.error("Error clearing app cache", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while clearing app-cache");
        }
    }

    @GetMapping("/users-opted-in-sentiment-analysis")
    @Operation(description = "Fetches those Users who opted for SentimentAnalysis")
    public ResponseEntity<List<User>> getUsersOptedForSA() {
        try {
            log.info("GET /admin/users-opted-in-sentiment-analysis invoked");

            List<User> userWithSA = userRepoImpl.findUserWithSA();

            if (userWithSA != null && !userWithSA.isEmpty()) {
                log.info("Retrieved {} users opted in for sentiment analysis", userWithSA.size());
                return ResponseEntity.ok(userWithSA);
            }

            log.info("No users found opted in for sentiment analysis");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("Error fetching users opted in for sentiment analysis", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

