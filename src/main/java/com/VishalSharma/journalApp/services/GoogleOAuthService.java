package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.repository.UserRepository;
import com.VishalSharma.journalApp.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class GoogleOAuthService {

    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    @Value("${spring.security.oauth2.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.registration.google.client-secret}")
    private String clientSecret;

    public GoogleOAuthService(RestTemplate restTemplate,
                              PasswordEncoder passwordEncoder,
                              UserRepository userRepository,
                              UserDetailsServiceImpl userDetailsService,
                              JwtUtil jwtUtil) {
        this.restTemplate = restTemplate;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    public String authenticate(String authCode) {
        try {
            String googleOAuthURL = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> detailsPayload = new LinkedMultiValueMap<>();
            detailsPayload.add("code", authCode);
            detailsPayload.add("client_id", clientId);
            detailsPayload.add("client_secret", clientSecret);
            detailsPayload.add("redirect_uri", "https://developers.google.com/oauthplayground");
            detailsPayload.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(detailsPayload, headers);
            ResponseEntity<Map> exchangedToken = restTemplate.postForEntity(googleOAuthURL, request, Map.class);

            if (exchangedToken.getStatusCode() != HttpStatus.OK || exchangedToken.getBody() == null) {
                log.error("Failed to exchange auth code with Google. Response: {}", exchangedToken);
                return null;
            }

            // Extract ID token
            String idToken = exchangedToken.getBody().get("id_token").toString();
            log.debug("Received ID Token from Google");

            // Verify user info
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

            if (userInfoResponse.getStatusCode() == HttpStatus.OK && userInfoResponse.getBody() != null) {
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = userInfo.get("email").toString();

                try {
                    userDetailsService.loadUserByUsername(email);
                    log.info("User {} found in database", email);
                } catch (Exception e) {
                    log.info("User {} not found, creating new user", email);
                    User user = new User();
                    user.setEmail(email);
                    user.setUserName(email);
                    user.setRoles(Arrays.asList("USER"));
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    userRepository.save(user);
                }

                // Generate and return JWT
                String jwtToken = jwtUtil.generateToken(email);
                log.info("Generated JWT for {}", email);
                return jwtToken;
            }

            log.error("Failed to fetch user info from ID token");
            return null;

        } catch (RestClientException e) {
            log.error("Error during Google OAuth authentication", e);
            throw new RuntimeException("Google OAuth authentication failed", e);
        }
    }
}
