package com.saadaoui.smartwarehouse.auth.controller;

import com.saadaoui.smartwarehouse.auth.dto.AuthResponse;
import com.saadaoui.smartwarehouse.auth.dto.LoginRequest;
import com.saadaoui.smartwarehouse.auth.dto.RegisterRequest;
import com.saadaoui.smartwarehouse.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authenticationService.login(request);
    }
}