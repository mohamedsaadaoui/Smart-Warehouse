package com.saadaoui.smartwarehouse.auth.service;

import com.saadaoui.smartwarehouse.user.dto.CreateUserRequest;
import com.saadaoui.smartwarehouse.user.dto.UpdateUserRequest;
import com.saadaoui.smartwarehouse.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    UserResponse getUserById(UUID id);

    Page<UserResponse> getAllUsers(String search, Boolean enabled, Pageable pageable);

    void deleteUser(UUID id, String currentUserEmail);

}
