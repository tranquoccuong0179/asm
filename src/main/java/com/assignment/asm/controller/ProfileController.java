package com.assignment.asm.controller;

import com.assignment.asm.dto.ApiResponse;
import com.assignment.asm.dto.request.RegistrationRequest;
import com.assignment.asm.dto.response.ProfileResponse;
import com.assignment.asm.service.ProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    ApiResponse<ProfileResponse> register(@RequestBody @Valid RegistrationRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .data(profileService.registration(request))
                .build();
    }

    @GetMapping("/profiles")
    ApiResponse<List<ProfileResponse>> getAllProfiles() {
        return ApiResponse.<List<ProfileResponse>>builder()
                .data(profileService.getAllProfiles())
                .build();
    }
}
