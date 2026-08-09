package com.saadaoui.smartwarehouse.user.dto;

import java.util.UUID;

public record UserOptionResponse(
        UUID id,
        String firstName,
        String lastName,
        String email
) {
}
