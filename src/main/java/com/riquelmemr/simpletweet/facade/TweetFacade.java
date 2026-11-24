package com.riquelmemr.simpletweet.facade;

import com.riquelmemr.simpletweet.dto.request.CreateTweetRequest;
import com.riquelmemr.simpletweet.dto.request.UpdateTweetRequest;
import com.riquelmemr.simpletweet.dto.response.FeedResponse;
import com.riquelmemr.simpletweet.dto.response.TweetDetailResponse;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface TweetFacade {

    TweetDetailResponse create(CreateTweetRequest dto, JwtAuthenticationToken token);

    void delete(Long id, JwtAuthenticationToken token);

    TweetDetailResponse update(Long id, UpdateTweetRequest request, JwtAuthenticationToken token);

    FeedResponse findByUserId(Long userId);

    FeedResponse getFeed(int page, int pageSize);
}
