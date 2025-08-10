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
    private UserRepositoryImpl userRespositoryImpl;

    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminName = authentication.getName();
        String msg = "Welcome " + adminName + "! Admin dashboard is working fine.";
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    //    get all users
    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        if (!allUsers.isEmpty()) {
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //create new admin
//    new user->admin
    @PostMapping("/create-new-admin")
    public ResponseEntity<String> createNewAdmin(@RequestBody User user) {
        try {
            userService.createNewAdmin(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Some error occurred while creating a new admin.", HttpStatus.CREATED);
        }
    }

//    existing user->admin

    @PatchMapping("/grant-as-admin/{userId}")
    public ResponseEntity<String> grantAsAdmin(@PathVariable ObjectId userId) {
        try {
            userService.grantAdminAuthority(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("user not found with id:" + userId, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/clear-app-cache")
    public ResponseEntity<Void> clearAppCache() {
        SecurityContextHolder.getContext().getAuthentication();
        appCache.init();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users-opted-in-sentiment-analysis")
    public ResponseEntity<List<User>> getUsersOptedForSA() {
        try {
            List<User> userWithSA = userRespositoryImpl.findUserWithSA();
            if (userWithSA != null) {
                return new ResponseEntity<>(userWithSA, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.info("Some error occurred while searching for users opted for sentimentAnalysis", e);
            throw new RuntimeException(e);
        }
    }


}
