package com.assignment.asm.controller;

import com.assignment.asm.dto.ApiResponse;
import com.assignment.asm.dto.request.RegistrationRequest;
import com.assignment.asm.dto.response.ProfileResponse;
import com.assignment.asm.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {
    ProfileService profileService;


    @PostMapping("/register")
    @Operation(summary = "API đăng kí tài khoản mới")
    ApiResponse<ProfileResponse> register(@RequestBody @Valid RegistrationRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .data(profileService.registration(request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/profiles")
    ApiResponse<List<ProfileResponse>> getAllProfiles() {
        return ApiResponse.<List<ProfileResponse>>builder()
                .data(profileService.getAllProfiles())
                .build();
    }
}
