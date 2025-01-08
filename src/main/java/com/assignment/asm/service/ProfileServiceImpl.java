package com.assignment.asm.service;

import com.assignment.asm.dto.keycloak.*;
import com.assignment.asm.dto.request.ChangePasswordRequest;
import com.assignment.asm.dto.request.LoginRequest;
import com.assignment.asm.dto.request.RegistrationRequest;
import com.assignment.asm.dto.request.UpdateUserRequest;
import com.assignment.asm.dto.response.LoginResponse;
import com.assignment.asm.dto.response.ProfileResponse;
import com.assignment.asm.dto.response.UpdateUserResponse;
import com.assignment.asm.exception.AppException;
import com.assignment.asm.exception.ErrorCode;
import com.assignment.asm.exception.ErrorNormalizer;
import com.assignment.asm.mapper.ProfileMapper;
import com.assignment.asm.model.Profile;
import com.assignment.asm.repository.KeyCloakRepository;
import com.assignment.asm.repository.ProfileRepository;
import com.assignment.asm.utils.AuthenUtil;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileServiceImpl implements ProfileService {
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    KeyCloakRepository keyCloakRepository;
    ErrorNormalizer errorNormalizer;
    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;
    @Override
    public List<ProfileResponse> getAllProfiles() {
        var profiles = profileRepository.findAll();
        return profiles.stream().map(profileMapper::toProfileResponse).toList();
    }

    @Override
    public ProfileResponse registration(RegistrationRequest request) {
        try {
            // Create account in KeyCloak
            // Exchange client Token
            var token = keyCloakRepository.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build());

            log.info("TokenInfo {}", token);
            // Create user with client Token and given info
            // Create user keycloak
            var creationResponse = keyCloakRepository.createUser(
                    "Bearer " + token.getAccessToken(),
                    UserCreationParam.builder()
                            .username(request.getUsername())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .email(request.getEmail())
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(Credential.builder()
                                    .type("password")
                                    .temporary(false)
                                    .value(request.getPassword())
                                    .build()))
                            .build());

            String userId = extractUserId(creationResponse);
            // Get userId of keyCloak account
            log.info("UserId {}", userId);

            var profile = profileMapper.toProfile(request);
            profile.setUserId(userId);

            profile = profileRepository.save(profile);

            return profileMapper.toProfileResponse(profile);
        }catch (FeignException exception){
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public ProfileResponse getProfileById() {
        String profileId = AuthenUtil.getProfileId();
        Profile profile = profileRepository.findByUserId(profileId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return profileMapper.toProfileResponse(profile);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            var token = keyCloakRepository.exchangeToken(LoginRequestParam.builder()
                    .grant_type("password")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .scope("openid")
                    .build());

            return LoginResponse.builder()
                    .accessToken(token.getAccessToken())
                    .refreshToken(token.getRefreshToken())
                    .username(request.getUsername())
                    .build();
        } catch (FeignException e) {
            throw errorNormalizer.handleKeyCloakException(e);
        }
    }

    @Override
    public boolean deleteProfile(UUID id) {
        var profile = profileRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var token = keyCloakRepository.exchangeToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_id(clientId)
                .client_secret(clientSecret)
                .scope("openid")
                .build());
        var response = keyCloakRepository.deleteUser(
                "Bearer " + token.getAccessToken(),
                profile.getUserId());
        profileRepository.delete(profile);
        if (response == null) {
            return false;
        }
        return true;
    }

    @Override
    public UpdateUserResponse updateUser(UpdateUserRequest request) {
        try {
            String userId = AuthenUtil.getProfileId();
            var profile = profileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            var token = keyCloakRepository.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build());
            var response = keyCloakRepository.updateUser(
                    "Bearer " + token.getAccessToken(),
                    userId,
                    UpdateRequestParam.builder()
                            .email(request.getEmail())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .build());
            if (response == null) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
            profile.setFirstName(request.getFirstName());
            profile.setLastName(request.getLastName());
            profile.setEmail(request.getEmail());
            profile.setAddress(request.getAddress());
            profile.setDob(request.getDob());
            profile.setPhone(request.getPhone());
//            profile = profileMapper.toUpdateProfile(request);
            profileRepository.save(profile);
            return profileMapper.toUpdateUserResponse(profile);
        }catch (FeignException exception){
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public boolean changePassword(ChangePasswordRequest newPassword) {
        String userId = AuthenUtil.getProfileId();
        var profile = profileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        System.out.println(userId);
        var token = keyCloakRepository.exchangeToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_id(clientId)
                .client_secret(clientSecret)
                .scope("openid")
                .build());
        var response = keyCloakRepository.changePassword(
                "Bearer " + token.getAccessToken(),
                userId,
                Credential.builder()
                        .type("password")
                        .temporary(false)
                        .value(newPassword.getNewPassword())
                        .build());
        if (response == null) {
            return false;
        }
        return true;
    }

    private String extractUserId(ResponseEntity<?> response){
        String location = response.getHeaders().get("Location").get(0);
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }
}
