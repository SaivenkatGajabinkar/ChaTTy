package com.chaty.service;

import com.chaty.dto.AuthResponse;
import com.chaty.dto.LoginRequest;
import com.chaty.dto.RegisterRequest;
import com.chaty.model.User;
import com.chaty.repository.UserRepository;
import com.chaty.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User(request.getName(), 
                             request.getEmail(),
                             passwordEncoder.encode(request.getPassword()),
                             request.getPreferredLanguage());

        userRepository.save(user);
        
        String jwt = jwtUtils.generateJwtToken(user.getId());
        
        return new AuthResponse(jwt, user.getId(), user.getName(), user.getPreferredLanguage());
    }

    public AuthResponse authenticateUser(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String jwt = jwtUtils.generateJwtToken(user.getId());
                return new AuthResponse(jwt, user.getId(), user.getName(), user.getPreferredLanguage());
            } else {
                throw new RuntimeException("Error: Invalid password!");
            }
        } else {
            throw new RuntimeException("Error: User not found!");
        }
    }
    
    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public AuthResponse googleLogin(String token) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList("YOUR_GOOGLE_CLIENT_ID"))
                .build();

            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");

                Optional<User> userOpt = userRepository.findByEmail(email);
                User user;
                if (userOpt.isPresent()) {
                    user = userOpt.get();
                } else {
                    user = new User(name, email, null, "en");
                    userRepository.save(user);
                }

                String jwt = jwtUtils.generateJwtToken(user.getId());
                return new AuthResponse(jwt, user.getId(), user.getName(), user.getPreferredLanguage());
            } else {
                throw new RuntimeException("Error: Invalid Google token!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error: Google authentication failed", e);
        }
    }
}
