package com.riquelmemr.simpletweet.controller;

import com.riquelmemr.simpletweet.dto.response.LikeDetailResponse;
import com.riquelmemr.simpletweet.facade.LikeFacade;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tweets")
@RequiredArgsConstructor
public class LikeController extends BaseController {

    private final LikeFacade likeFacade;

    @PostMapping("/{tweetId}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable final Long tweetId,
            JwtAuthenticationToken jwtAuthenticationToken
    ) {
        likeFacade.createOrDelete(tweetId, jwtAuthenticationToken);
        return handleResponse(HttpStatus.OK, null);
    }

    @GetMapping("/{tweetId}/likes")
    public ResponseEntity<List<LikeDetailResponse>> getLikesByTweet(
            @PathVariable final Long tweetId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "50") int pageSize
    ) {
        List<LikeDetailResponse> likes = likeFacade.getLikesByTweet(tweetId, page, pageSize);
        return handleResponse(HttpStatus.OK, likes);
    }
}
