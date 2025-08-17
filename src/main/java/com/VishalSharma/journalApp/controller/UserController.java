package com.VishalSharma.journalApp.controller;

import com.VishalSharma.journalApp.dto.UserDTO;
import com.VishalSharma.journalApp.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "User APIs", description = "Update, Delete User")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Health-check
    @GetMapping("/dashboard")
    @Operation(description = "User dashboard")
    public ResponseEntity<String> dashboard() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("GET /user/dashboard invoked by user: {}", userName);

            String msg = "Welcome " + userName + "! User dashboard is working fine.";
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            log.error("Error accessing /user/dashboard", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong with user dashboard");
        }
    }

    // Update user credentials
    @PutMapping("/update-user-credentials")
    @Operation(description = "Updates existing user's credentials after login")
    public ResponseEntity<String> updateUserCredentials(@RequestBody UserDTO user) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("PUT /user/update-user-credentials invoked for user: {}", userName);

            userService.updateCredentials(user, userName);

            log.info("Credentials updated successfully for user: {}", userName);
            return ResponseEntity.ok("User credentials updated successfully.");
        } catch (Exception e) {
            log.error("Error updating credentials for user: {}", user.getUserName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while updating credentials");
        }
    }

    // Delete user
    @DeleteMapping("/delete-user")
    @Operation(description = "Deletes an existing user after login")
    public ResponseEntity<String> deleteUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("DELETE /user/delete-user invoked for user: {}", userName);

            userService.deleteUserByUserName(userName);

            log.info("User deleted successfully: {}", userName);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (Exception e) {
            log.error("Error deleting user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while deleting user");
        }
    }
}
