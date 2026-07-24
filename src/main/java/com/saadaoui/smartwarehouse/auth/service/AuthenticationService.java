package com.saadaoui.smartwarehouse.auth.service;

import com.saadaoui.smartwarehouse.auth.dto.AuthResponse;
import com.saadaoui.smartwarehouse.auth.dto.LoginRequest;
import com.saadaoui.smartwarehouse.auth.dto.RegisterRequest;

public interface AuthenticationService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}
