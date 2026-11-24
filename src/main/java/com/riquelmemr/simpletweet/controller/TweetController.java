package com.riquelmemr.simpletweet.controller;

import com.riquelmemr.simpletweet.dto.request.CreateTweetRequest;
import com.riquelmemr.simpletweet.dto.request.UpdateTweetRequest;
import com.riquelmemr.simpletweet.dto.response.FeedResponse;
import com.riquelmemr.simpletweet.dto.response.TweetDetailResponse;
import com.riquelmemr.simpletweet.facade.TweetFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tweets")
@RequiredArgsConstructor
public class TweetController extends BaseController {

    private final TweetFacade tweetFacade;

    @PostMapping("/create")
    public ResponseEntity<TweetDetailResponse> createTweet(
            @Valid @RequestBody CreateTweetRequest request,
            JwtAuthenticationToken token
    ) {
        TweetDetailResponse tweet = tweetFacade.create(request, token);
        return handleResponse(HttpStatus.CREATED, tweet);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable Long id, JwtAuthenticationToken token) {
        tweetFacade.delete(id, token);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TweetDetailResponse> updateTweet(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTweetRequest request,
            JwtAuthenticationToken token
    ) {
        TweetDetailResponse tweet = tweetFacade.update(id, request, token);
        return handleResponse(HttpStatus.OK, tweet);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<FeedResponse> findByUser(@PathVariable Long userId) {
        FeedResponse userFeed = tweetFacade.findByUserId(userId);
        return handleResponse(HttpStatus.OK, userFeed);
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedResponse> getFeed(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        FeedResponse feed = tweetFacade.getFeed(page, pageSize);
        return handleResponse(HttpStatus.OK, feed);
    }
}
