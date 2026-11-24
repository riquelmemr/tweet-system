package com.riquelmemr.simpletweet.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateTweetRequest(
        @NotBlank String content
) {
}
