package com.booking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // These paths bypass JWT check entirely
    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/auth/register",
        "/api/auth/login"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        System.out.println("=== JwtFilter HIT ===");
        System.out.println("URI = " + request.getRequestURI());
        System.out.println("Auth Header = " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String email;

        try {
            email = jwtUtil.extractEmail(jwt);
            System.out.println("Email = " + email);
        } catch (Exception e) {
            System.out.println("JWT Error = " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (email != null &&
            SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails =
                userDetailsService.loadUserByUsername(email);

            boolean valid = jwtUtil.isTokenValid(jwt, userDetails);

            System.out.println("Token Valid = " + valid);

            if (valid) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                        .buildDetails(request)
                );
                SecurityContextHolder.getContext()
                .setAuthentication(authToken);

            System.out.println("Authentication SET");
            }
        }

        filterChain.doFilter(request, response);
    }
}