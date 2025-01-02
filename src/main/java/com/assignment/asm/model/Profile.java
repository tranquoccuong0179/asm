package com.assignment.asm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    //Keycloak userId
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dob;
}
