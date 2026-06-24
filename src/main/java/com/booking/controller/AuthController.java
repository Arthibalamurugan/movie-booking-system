package com.booking.controller;

import com.booking.dto.request.LoginRequest;
import com.booking.dto.request.RegisterRequest;
import com.booking.dto.response.ApiResponse;
import com.booking.dto.response.AuthResponse;
import com.booking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.booking.dto.request.ForgotPasswordRequest;
import com.booking.dto.request.ResetPasswordRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", response));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>>
    forgotPassword(
            @RequestBody
            ForgotPasswordRequest request) {

        authService.forgotPassword(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Password reset link sent",
                        "Success"
                )
        );
    }  
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>>
    resetPassword(
            @RequestBody
            ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Password reset successful",
                        "Success"
                )
        );
    }
}