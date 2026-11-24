package com.riquelmemr.simpletweet.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateTweetRequest(
        @NotBlank String content
) {
}
