package com.tx.security;

import com.tx.Model.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class RemoteUserDetailsService implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
            System.out.println("Calling Auth Service with: " + encodedUsername);
            AuthUser user = restTemplate.getForObject(
                    "http://localhost:8082/auth/user/" + encodedUsername,
                    AuthUser.class
            );
            System.out.println("USER RECEIVED FROM AUTH SERVICE: " + user);

            return new User(
                    user.getUsername(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
            );
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found in Auth Service");
        }
    }

}
