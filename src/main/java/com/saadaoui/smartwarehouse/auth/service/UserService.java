package com.saadaoui.smartwarehouse.auth.service;

import com.saadaoui.smartwarehouse.auth.dto.RegisterRequest;
import com.saadaoui.smartwarehouse.auth.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User register(RegisterRequest request);

    User getById(UUID id);

    User getByEmail(String email);

    List<User> getAll();

    void delete(UUID id);

}