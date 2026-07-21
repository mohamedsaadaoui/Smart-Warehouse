package com.saadaoui.smartwarehouse.auth.service.impl;

import com.saadaoui.smartwarehouse.auth.dto.RegisterRequest;
import com.saadaoui.smartwarehouse.auth.entity.Role;
import com.saadaoui.smartwarehouse.auth.entity.User;
import com.saadaoui.smartwarehouse.auth.repository.RoleRepository;
import com.saadaoui.smartwarehouse.auth.repository.UserRepository;
import com.saadaoui.smartwarehouse.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .roles(Set.of(role))
                .build();

        return userRepository.save(user);
    }

    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
