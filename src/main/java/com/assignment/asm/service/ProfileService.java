package com.assignment.asm.service;

import com.assignment.asm.dto.request.LoginRequest;
import com.assignment.asm.dto.request.RegistrationRequest;
import com.assignment.asm.dto.response.LoginResponse;
import com.assignment.asm.dto.response.ProfileResponse;

import java.util.List;

public interface ProfileService {
    public List<ProfileResponse> getAllProfiles();
    public ProfileResponse registration(RegistrationRequest request);
    public ProfileResponse getProfileById();
    public LoginResponse login(LoginRequest request);
}
