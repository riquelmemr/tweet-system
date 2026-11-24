package com.riquelmemr.simpletweet.facade.impl;

import com.riquelmemr.simpletweet.dto.request.CreateTweetRequest;
import com.riquelmemr.simpletweet.dto.request.UpdateTweetRequest;
import com.riquelmemr.simpletweet.dto.response.FeedResponse;
import com.riquelmemr.simpletweet.dto.response.TweetDetailResponse;
import com.riquelmemr.simpletweet.facade.TweetFacade;
import com.riquelmemr.simpletweet.mapper.TweetMapper;
import com.riquelmemr.simpletweet.model.Tweet;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.service.tweet.TweetService;
import com.riquelmemr.simpletweet.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class TweetFacadeImpl implements TweetFacade {

    private final TweetService tweetService;
    private final TweetMapper tweetMapper;
    private final UserService userService;

    @Override
    public TweetDetailResponse create(CreateTweetRequest dto, JwtAuthenticationToken token) {
        User user = userService.extractUserFromToken(token);
        Tweet tweet = tweetMapper.toModel(dto, user);
        tweetService.create(tweet);
        return tweetMapper.toTweetDetailResponseDto(tweet);
    }

    @Override
    public void delete(Long id, JwtAuthenticationToken token) {
        User user = userService.extractUserFromToken(token);
        tweetService.deleteById(id, user);
    }

    @Override
    public TweetDetailResponse update(Long id, UpdateTweetRequest request, JwtAuthenticationToken token) {
        User user = userService.extractUserFromToken(token);
        Tweet tweet = tweetService.update(id, request, user);
        return tweetMapper.toTweetDetailResponseDto(tweet);
    }

    @Override
    public FeedResponse findByUserId(Long userId) {
        User user = userService.findById(userId);
        List<Tweet> tweets = tweetService.findByUserId(user.getPk());
        return tweetMapper.toFeedResponseDto(tweets);
    }

    @Override
    public FeedResponse getFeed(int page, int pageSize) {
        Page<Tweet> tweets = tweetService.findAll(page, pageSize);
        return tweetMapper.toFeedResponseDto(tweets);
    }
}
