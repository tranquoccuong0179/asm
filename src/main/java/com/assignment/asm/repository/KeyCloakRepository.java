package com.assignment.asm.repository;

import com.assignment.asm.dto.keycloak.TokenExchangeParam;
import com.assignment.asm.dto.keycloak.TokenExchangeResponse;
import com.assignment.asm.dto.keycloak.UserCreationParam;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-client", url = "http://localhost:8081")
public interface KeyCloakRepository {
    @PostMapping(value = "/realms/Assignment/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam param);

    @PostMapping(value = "/admin/realms/Assignment/users",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(
            @RequestHeader("authorization") String token,
            @RequestBody UserCreationParam param);
}
