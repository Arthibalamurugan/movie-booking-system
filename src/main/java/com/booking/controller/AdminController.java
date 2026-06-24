package com.booking.controller;

import com.booking.dto.response.AnalyticsResponse;
import com.booking.dto.response.ApiResponse;
import com.booking.dto.response.RevenueResponse;
import com.booking.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.booking.dto.response.UserResponse;
import com.booking.dto.request.UpdateRoleRequest;
import com.booking.dto.response.UserResponse;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Users fetched successfully",
                        adminService.getAllUsers()
                )
        );
    } 
    
    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>>
    updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateRoleRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User role updated successfully",
                        adminService.updateUserRole(id, request)
                )
        );
    }
    
    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RevenueResponse>> getRevenue() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Revenue fetched",
                        adminService.getRevenue()
                )
        );
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Analytics fetched",
                        adminService.getAnalytics()
                )
        );
    }
}