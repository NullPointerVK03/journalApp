package com.VishalSharma.journalApp.controller;


import com.VishalSharma.journalApp.appCache.AppCache;
import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.repository.UserRepository;
import com.VishalSharma.journalApp.repository.UserRepositoryImpl;
import com.VishalSharma.journalApp.services.UserService;
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
    public ResponseEntity<String> dashboard() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminName = authentication.getName();
            log.info("Health of AdminController dashboard is ok for admin: {}", adminName);
            String msg = "Welcome " + adminName + "! Admin dashboard is working fine.";
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error accessing AdminController dashboard", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Fetching all users from repository");
        List<User> allUsers = userRepository.findAll();
        if (!allUsers.isEmpty()) {
            log.info("Found {} users", allUsers.size());
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }
        log.warn("No users found in repository");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-new-admin")
    public ResponseEntity<String> createNewAdmin(@RequestBody User user) {
        try {
            log.info("Incoming POST request to create a new admin with username: {}", user.getUserName());
            userService.createNewAdmin(user);
            log.info("Admin created successfully with username: {}", user.getUserName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error occurred while creating a new admin with username: {}", user.getUserName(), e);
            return new ResponseEntity<>("Some error occurred while creating a new admin.", HttpStatus.CREATED);
        }
    }

    @PatchMapping("/grant-as-admin/{userId}")
    public ResponseEntity<String> grantAsAdmin(@PathVariable ObjectId userId) {
        try {
            log.info("Granting admin authority to user with ID: {}", userId);
            userService.grantAdminAuthority(userId);
            log.info("Admin authority granted successfully for user ID: {}", userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.warn("User not found while granting admin authority. ID: {}", userId, e);
            return new ResponseEntity<>("user not found with id:" + userId, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/clear-app-cache")
    public ResponseEntity<Void> clearAppCache() {
        try {
            log.info("Clearing application cache");
            appCache.init();
            log.info("Application cache cleared successfully");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while clearing application cache", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/users-opted-in-sentiment-analysis")
    public ResponseEntity<List<User>> getUsersOptedForSA() {
        try {
            log.info("Fetching users who opted in for sentiment analysis");
            List<User> userWithSA = userRepoImpl.findUserWithSA();
            if (userWithSA != null && !userWithSA.isEmpty()) {
                log.info("Found {} users opted in for sentiment analysis", userWithSA.size());
                return new ResponseEntity<>(userWithSA, HttpStatus.OK);
            }
            log.warn("No users found opted in for sentiment analysis");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error occurred while searching for users opted in for sentiment analysis", e);
            throw new RuntimeException(e);
        }
    }
}
