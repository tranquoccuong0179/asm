package com.assignment.asm.service;

import com.assignment.asm.dto.request.LoginRequest;
import com.assignment.asm.dto.request.RegistrationRequest;
import com.assignment.asm.dto.response.LoginResponse;
import com.assignment.asm.dto.response.ProfileResponse;

import java.util.List;
import java.util.UUID;

public interface ProfileService {
    public List<ProfileResponse> getAllProfiles();
    public ProfileResponse registration(RegistrationRequest request);
    public ProfileResponse getProfileById();
    public LoginResponse login(LoginRequest request);
    public boolean deleteProfile(UUID id);
}
