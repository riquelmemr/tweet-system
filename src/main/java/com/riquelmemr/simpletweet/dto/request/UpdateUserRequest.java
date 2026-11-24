package com.riquelmemr.simpletweet.dto.request;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        String username,
        @Email String email,
        String name,
        String bio
) {
}
