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
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AppCache appCache;

    @Autowired
    private UserRepositoryImpl userRepoImpl;

    @GetMapping("/dashboard")
    @Operation(description = "Admin dashboard")
    public ResponseEntity<String> dashboard() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminName = authentication.getName();

            log.info("Incoming GET request to access admin dashboard by admin: {}", adminName);
            String msg = "Welcome " + adminName + "! Admin dashboard is working fine.";

            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error accessing AdminController dashboard", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/all-users")
    @Operation(description = "Fetches all users from DB")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminName = authentication.getName();

            log.info("Incoming GET request to access all users in DB by admin: {}", adminName);

            log.info("Fetching all users from repository");
            List<User> allUsers = userRepository.findAll();

            if (!allUsers.isEmpty()) {
                log.info("Found {} users", allUsers.size());
                return new ResponseEntity<>(allUsers, HttpStatus.OK);
            }
            log.warn("No users found in repository");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Something went wrong while fetching all users from DB. Exception: ", e);
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/create-new-admin")
    @Operation(description = "Creates a new admin")
    public ResponseEntity<String> createNewAdmin(@RequestBody UserDTO user) {
        try {
            log.info("Incoming POST request to create a new admin with username: {}", user.getUserName());
            userService.createNewAdmin(user);

            log.info("Admin created with username: {}", user.getUserName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error occurred while creating a new admin with username: {}. Exception: ", user.getUserName(), e);
            return new ResponseEntity<>("Some error occurred while creating a new admin.", HttpStatus.CREATED);
        }
    }

    @PatchMapping("/grant-as-admin/{userId}")
    @Operation(description = "Grants admin authority to a existing user")
    public ResponseEntity<String> grantAsAdmin(@PathVariable String userId) {
        try {
            log.info("Incoming PATCH request to grant a existing User as a new admin with uId: {}", userId);

            log.info("Converting data type of userId from String to ObjectId");
            ObjectId id = new ObjectId(userId);

            log.info("Granting admin authority to user with ID: {}", userId);
            userService.grantAdminAuthority(id);

            log.info("Admin authority granted successfully for user ID: {}", id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Something went wrong while granting admin authority. userId: {}. Exception: ", userId, e);
            return new ResponseEntity<>("user not found with id:" + userId + " or Something went wrong.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/clear-app-cache")
    @Operation(description = "Clears previous app-cache and loaded latest app cache values")
    public ResponseEntity<String> clearAppCache() {
        try {
            log.info("Incoming GET request to clear app cache by a admin.");

            log.info("Clearing application cache");
            appCache.init();

            log.info("Application cache cleared successfully");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while clearing application cache. Exception: ", e);
            return new ResponseEntity<>("Something went wrong while clearing app-cache", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users-opted-in-sentiment-analysis")
    @Operation(description = "Fetches those Users who opted for SentimentAnalysis")
    public ResponseEntity<List<User>> getUsersOptedForSA() {
        try {
            log.info("Incoming GET request to get users who are opted-in for sentiment analysis.");

            log.info("Fetching users who opted in for sentiment analysis");
            List<User> userWithSA = userRepoImpl.findUserWithSA();

            if (userWithSA != null && !userWithSA.isEmpty()) {
                log.info("Found {} users opted in for sentiment analysis", userWithSA.size());
                return new ResponseEntity<>(userWithSA, HttpStatus.OK);
            }

            log.info("No users found opted in for sentiment analysis");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Something went wrong while fetching users who opted-in for sentiment analysis. Exception: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
