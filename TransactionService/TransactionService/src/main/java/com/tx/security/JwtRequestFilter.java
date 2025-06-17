package com.tx.security;

import com.tx.Util.Jwtutil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final Jwtutil jwtutil;

    public JwtRequestFilter(UserDetailsService userDetailsService, Jwtutil jwtutil) {
        this.userDetailsService = userDetailsService;
        this.jwtutil = jwtutil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        System.out.println("🚨 JwtRequestFilter triggered for URI: " + uri);

        // ⛔️ Skip token processing for public endpoints
        if (uri.equals("/transaction/test")) {
            chain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("🔑 JWT Token Extracted: " + jwt);
            try {
                username = jwtutil.extractUsername(jwt);
                System.out.println("👤 Extracted Username: " + username);
            } catch (Exception e) {
                System.out.println("❌ Failed to extract username: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ No Bearer token found in Authorization header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("Authorities: " + userDetails.getAuthorities());

                System.out.println("✅ Loaded user details for: " + userDetails.getUsername());

                if (jwtutil.validateToken(jwt, userDetails)) {
                    System.out.println("✅ Token validated successfully");

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("❌ Token validation failed");
                }
            } catch (Exception e) {
                System.out.println("❌ User loading or validation error: " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }

}
