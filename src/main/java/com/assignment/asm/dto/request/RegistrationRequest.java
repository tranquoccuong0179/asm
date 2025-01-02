package com.assignment.asm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String username;
    private String firstName;
    private String password;
    private String lastName;
    private LocalDate dob;
}
