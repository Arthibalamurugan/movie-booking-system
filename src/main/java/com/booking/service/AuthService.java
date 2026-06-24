package com.booking.service;

import com.booking.dto.request.LoginRequest;
import com.booking.dto.request.RegisterRequest;
import com.booking.dto.response.AuthResponse;
import com.booking.entity.User;
import com.booking.enums.Role;
import com.booking.exception.EmailAlreadyExistsException;
import com.booking.repository.UserRepository;
import com.booking.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.booking.entity.PasswordResetToken;
import com.booking.dto.request.ForgotPasswordRequest;
import com.booking.dto.request.ResetPasswordRequest;
import com.booking.repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {

        // 1. Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // 2. Build and save user — password is BCrypt hashed
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)   // default role on registration
                .build();

        userRepository.save(user);

        // 3. Load as UserDetails and generate JWT
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        // 1. Authenticate — throws BadCredentialsException if wrong password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Load user and generate fresh JWT
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
    public void forgotPassword(
            ForgotPasswordRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        String token =
                UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                PasswordResetToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(
                                LocalDateTime.now()
                                        .plusMinutes(15))
                        .build();

        tokenRepository.save(resetToken);

        String resetLink =
                "http://localhost:3000/reset-password/"
                        + token;

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink
        );
    }
    public void resetPassword(
            ResetPasswordRequest request) {

        PasswordResetToken token =
                tokenRepository.findByToken(
                        request.getToken())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid token"));

        if (token.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Token expired");
        }

        User user = token.getUser();

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()));

        userRepository.save(user);

        tokenRepository.delete(token);
    }
}