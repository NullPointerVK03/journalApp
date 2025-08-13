package com.VishalSharma.journalApp.service;

import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.repository.UserRepository;
import com.VishalSharma.journalApp.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class UserDeltailsImplTests {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;


    @Test
    void testLoadUserByUsername() {

        doReturn(
                User.builder()
                        .userName("Ak")
                        .password("abcde")
                        .roles(Arrays.asList("USER"))
                        .build()
        ).when(userRepository).findByUserName("Ak");
        UserDetails userDetails = userDetailsService.loadUserByUsername("Ak");
        Assertions.assertNotNull(userDetails.getAuthorities());
    }

}
