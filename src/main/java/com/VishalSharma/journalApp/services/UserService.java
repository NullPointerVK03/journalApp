package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalEntryService journalEntryService;


    //    CRUD operations

    public void createNewUser(User user) {
        try {
            user.getRoles().add("USER");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        } catch (Exception e) {
//            THE EXPECTED EXCEPTIONS -> DUPLICATE USERNAME, DUPLICATE EMAIL
            throw new RuntimeException(e);
        }
    }

    public void updateCredentials(User user, String userName) {
        try {
            User userInDb = userRepository.findByUserName(userName);
            userInDb.setUserName(user.getUserName());
            userInDb.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(userInDb);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteUserByUserName(String userName) {
        try {
            journalEntryService.deleteAllJournalsOfUser(userName);
            userRepository.deleteByUserName(userName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    public void createNewAdmin(User admin) {
        try {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            admin.getRoles().addAll(Arrays.asList("USER", "ADMIN"));
            userRepository.save(admin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void grantAdminAuthority(ObjectId userId) {
        Optional<User> userInDb = userRepository.findById(userId);
        if (userInDb.isPresent()) {
            userInDb.get().getRoles().add("ADMIN");
            userRepository.save(userInDb.get());
        } else throw new RuntimeException();
    }
}
