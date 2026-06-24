package com.booking.controller;

import com.booking.dto.request.CreateShowRequest;
import com.booking.dto.request.UpdateShowRequest;
import com.booking.dto.response.ApiResponse;
import com.booking.dto.response.ShowResponse;
import com.booking.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;
@RestController
@RequestMapping("/api/admin/shows")
@RequiredArgsConstructor
public class AdminShowController {

    private final ShowService showService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ShowResponse>> createShow(
            @RequestBody CreateShowRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Show created",
                        showService.createShow(request)));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ShowResponse>> updateShow(
            @PathVariable Long id,
            @RequestBody UpdateShowRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Show updated successfully",
                        showService.updateShow(id, request)
                )
        );
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteShow(
            @PathVariable Long id) {

        showService.deleteShow(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Show deleted successfully",
                        "Deleted"
                )
        );
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getAllShows() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Shows fetched successfully",
                        showService.getAllShows()
                )
        );
    }
}