package com.VishalSharma.journalApp.controller;

import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //    health-check
    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        String msg = "Welcome " + userName + "! User dashboard is working fine.";
        return new ResponseEntity<>(msg
                , HttpStatus.OK);
    }

//    update, delete
//    update

    @PutMapping("/update-user-credentials")
    public ResponseEntity<Void> updateUserCredentials(@RequestBody User user) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            log.info("Incoming PUT request to update user's credentials for userName: {}", userName);
            userService.updateCredentials(user, userName);
            log.info("Credentials updated for userName: {}", userName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Something went wrong while updating userCredentials. Exception: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //    delete
    @DeleteMapping("/delete-user")
    public ResponseEntity<Void> deleteUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            log.info("Incoming DELETE request to delete user with userName: {}", userName);
            userService.deleteUserByUserName(userName);
            log.info("User with userName: {} deleted from DB.", userName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Something went wrong while deleting user. Exception: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
