package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.dto.UserDTO;
import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JournalEntryService journalEntryService;

    public UserService(UserRepository userRepository, JournalEntryService journalEntryService) {
        this.userRepository = userRepository;
        this.journalEntryService = journalEntryService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    //    CRUD operations

    public void createNewUser(UserDTO user) {
        try {
            log.info("Creating new user with username: {}", user.getUserName());
            User newUser = new User();
            newUser.setUserName(user.getUserName());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            newUser.setEmail(user.getEmail());
            newUser.setSentimentAnalysis(user.isSentimentAnalysis());
            newUser.getRoles().add("USER");
            userRepository.save(newUser);
            log.info("User with username: {} created successfully", user.getUserName());
        } catch (Exception e) {
            log.error("Error occurred while creating new user with username: {}", user.getUserName(), e);
            throw new RuntimeException(e);
        }
    }

    public void updateCredentials(UserDTO user, String userName) {
        try {
            log.info("Updating credentials for username: {}", userName);
            User userInDb = userRepository.findByUserName(userName);
            userInDb.setUserName(user.getUserName());
            userInDb.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(userInDb);
            log.info("Credentials updated successfully for username: {}", userName);
        } catch (Exception e) {
            log.error("Error occurred while updating credentials for username: {}", userName, e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteUserByUserName(String userName) {
        try {
            log.info("Deleting user with username: {}", userName);
            journalEntryService.deleteAllJournalsOfUser(userName);
            userRepository.deleteByUserName(userName);
            log.info("User with username: {} deleted successfully", userName);
        } catch (Exception e) {
            log.error("Error occurred while deleting user with username: {}", userName, e);
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void createNewAdmin(UserDTO user) {
        try {
            log.info("Creating new admin with username: {}", user.getUserName());
            User admin = new User();
            admin.setUserName(user.getUserName());
            admin.setPassword(passwordEncoder.encode(user.getPassword()));
            admin.setEmail(user.getEmail());
            admin.getRoles().addAll(Arrays.asList("USER", "ADMIN"));
            userRepository.save(admin);
            log.info("Admin with username: {} created successfully", admin.getUserName());
        } catch (Exception e) {
            log.error("Error occurred while creating new admin with username: {}", user.getUserName(), e);
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void grantAdminAuthority(ObjectId userId) {
        log.info("Granting ADMIN authority to user with ID: {}", userId);
        Optional<User> userInDb = userRepository.findById(userId);
        if (userInDb.isPresent()) {
            userInDb.get().getRoles().add("ADMIN");
            userRepository.save(userInDb.get());
            log.info("ADMIN authority granted successfully to user ID: {}", userId);
        } else {
            log.warn("User with ID: {} not found while granting ADMIN authority", userId);
            throw new RuntimeException("User not found for ID: " + userId);
        }
    }
}
