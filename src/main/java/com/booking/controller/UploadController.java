package com.booking.controller;

import com.booking.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/admin/upload")
@RequiredArgsConstructor
public class UploadController {

    @PostMapping("/poster")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>>
    uploadPoster(
            @RequestParam("file")
            MultipartFile file
    ) throws Exception {

        String fileName =
                System.currentTimeMillis()
                        + "_"
                        + file.getOriginalFilename();

        Path uploadPath =
                Paths.get(
                        "uploads/posters/",
                        fileName
                );

        Files.createDirectories(
                uploadPath.getParent()
        );

        Files.write(
                uploadPath,
                file.getBytes()
        );

        String imageUrl =
                "http://localhost:8080/posters/"
                        + fileName;

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Poster uploaded",
                        imageUrl
                )
        );
    }
}