package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // constructor injection is cleaner than field injection
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("Attempting to load user with username: {}", username);

        User userInDb = userRepository.findByUserName(username);

        if (userInDb == null) {
            log.warn("User with username: {} not found", username);
            throw new UsernameNotFoundException("User with username " + username + " not found");
        }

        List<String> roles = userInDb.getRoles();
        log.info("User found. Username: {}, Roles: {}", username, roles);

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(userInDb.getUserName())
                .password(userInDb.getPassword())
                .authorities(roles.stream()
                        .map(role -> "ROLE_" + role)  // Ensure ROLE_ prefix
                        .map(SimpleGrantedAuthority::new)
                        .toList())
                .build();
    }
}
