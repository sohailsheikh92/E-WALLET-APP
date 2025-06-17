package com.auth.Controller;

import com.auth.Repo.AuthUserRepo;
import com.auth.model.AuthUser;
import com.auth.security.CustomUserDetailsService;
import com.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
//@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
@RestController
@RequestMapping("/auth")
public class AuthUserController {
    @Autowired
    private AuthUserRepo authUserRepo;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/seedata")
    public List<AuthUser> getData(){
        return userDetailsService.getData();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthUser loginRequest) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(Collections.singletonMap("token", jwt));
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Make sure this bean is defined

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        return ResponseEntity.ok(Collections.singletonMap("username", username));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthUser user) {
        try {
            if (authUserRepo.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Username already exists");
            }

            // ✅ Encode password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            authUserRepo.save(user);

            return ResponseEntity.ok("Registered successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed");
        }
    }



    // DELETE by username
    @DeleteMapping("/delete/username/{username}")
    public ResponseEntity<String> deleteByUsername(@PathVariable String username) {
        try {
            userDetailsService.deleteByUsername(username);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<AuthUser> getUser(@PathVariable String username) {
        System.out.println("USERNAME RECEIVED IN CONTROLLER: " + username);

        AuthUser user = authUserRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        return ResponseEntity.ok(user);
    }


}
