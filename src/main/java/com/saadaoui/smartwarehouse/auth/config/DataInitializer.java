package com.saadaoui.smartwarehouse.auth.config;

import com.saadaoui.smartwarehouse.auth.entity.Role;
import com.saadaoui.smartwarehouse.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        createRole("ADMIN");
        createRole("MANAGER");
        createRole("EMPLOYEE");

    }

    private void createRole(String roleName){

        if(!roleRepository.existsByName(roleName)){

            Role role = Role.builder()
                    .name(roleName)
                    .description(roleName + " Role")
                    .build();

            roleRepository.save(role);

        }

    }

}