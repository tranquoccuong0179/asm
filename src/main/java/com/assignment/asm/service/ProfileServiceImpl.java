package com.assignment.asm.service;

import com.assignment.asm.dto.keycloak.Credential;
import com.assignment.asm.dto.keycloak.TokenExchangeParam;
import com.assignment.asm.dto.keycloak.UserCreationParam;
import com.assignment.asm.dto.request.RegistrationRequest;
import com.assignment.asm.dto.response.ProfileResponse;
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

    private String extractUserId(ResponseEntity<?> response){
        String location = response.getHeaders().get("Location").get(0);
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }
}
