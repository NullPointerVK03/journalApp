package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)  {
        User userInDb = userRepository.findByUserName(username);
        if (userInDb != null) {
            List<String> roles = userInDb.getRoles();
            return org.springframework.security.core.userdetails
                    .User
                    .builder()
                    .username(userInDb.getUserName())
                    .password(userInDb.getPassword())
                    .authorities(roles.stream()
                            .map(role -> "ROLE_" + role)  // add ROLE_ prefix
                            .map(SimpleGrantedAuthority::new)
                            .toList())
                    .build();
        }

        throw new UsernameNotFoundException("User with username " + username + "not found");
    }
}
