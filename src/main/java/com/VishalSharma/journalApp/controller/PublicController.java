package com.VishalSharma.journalApp.controller;

import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.services.UserDetailsServiceImpl;
import com.VishalSharma.journalApp.services.UserService;
import com.VishalSharma.journalApp.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
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
    public ResponseEntity<String> dashboard() {
        try {
            log.info("Health of public dashboard is ok :) ");
            String msg = "Welcome! public dashboard is working fine.";
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.toString());
        }
    }

    @GetMapping("/get-logs")
    public void getLogs() {
        try {
            throw new RuntimeException();
        } catch (Exception e) {
            log.info("Logging successful");
        }
    }

    //    CRUD operations
//    creating new user
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody User user) {
        try {
            userService.createNewUser(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }catch (Exception e){
            log.error("Exception occurred while createAuthenticationToken ", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }
    }


