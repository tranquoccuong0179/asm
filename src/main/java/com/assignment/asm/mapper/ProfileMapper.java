package com.assignment.asm.mapper;

import com.assignment.asm.dto.request.RegistrationRequest;
import com.assignment.asm.dto.request.UpdateUserRequest;
import com.assignment.asm.dto.response.ProfileResponse;
import com.assignment.asm.dto.response.UpdateUserResponse;
import com.assignment.asm.model.Profile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toProfile(RegistrationRequest request);
    ProfileResponse toProfileResponse(Profile profile);
    Profile toUpdateProfile(UpdateUserRequest request);
    UpdateUserResponse toUpdateUserResponse(Profile profile);
}
