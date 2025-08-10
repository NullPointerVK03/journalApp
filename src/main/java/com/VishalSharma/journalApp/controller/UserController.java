package com.VishalSharma.journalApp.controller;

import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

//    health-check
@GetMapping("/dashboard")
public ResponseEntity<String> dashboard(){
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userName = authentication.getName();
    String msg = "Welcome " + userName + "! User dashboard is working fine.";
    return new ResponseEntity<>(msg
            , HttpStatus.OK);
}

//    update, delete
//    update

    @PutMapping("/update-user-credentials")
    public ResponseEntity<Void> updateUserCredentials(@RequestBody User user){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            userService.updateCredentials(user, userName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

//    delete
    @DeleteMapping("/delete-user")
    public ResponseEntity<Void> deleteUser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            userService.deleteUserByUserName(userName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
