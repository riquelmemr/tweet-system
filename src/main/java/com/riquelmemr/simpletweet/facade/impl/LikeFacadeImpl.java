package com.riquelmemr.simpletweet.facade.impl;

import com.riquelmemr.simpletweet.dto.response.LikeDetailResponse;
import com.riquelmemr.simpletweet.facade.LikeFacade;
import com.riquelmemr.simpletweet.mapper.LikeMapper;
import com.riquelmemr.simpletweet.model.Like;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.service.like.LikeService;
import com.riquelmemr.simpletweet.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LikeFacadeImpl implements LikeFacade {

    private final UserService userService;
    private final LikeService likeService;
    private final LikeMapper likeMapper;

    @Override
    public void createOrDelete(Long tweetId, JwtAuthenticationToken token) {
        User user = userService.extractUserFromToken(token);
        likeService.createOrDelete(tweetId, user);
    }

    @Override
    public List<LikeDetailResponse> getLikesByTweet(Long tweetId, int page, int pageSize) {
        Page<Like> likes = likeService.findByTweet(tweetId, page, pageSize);
        return likeMapper.toLikeDetailResponseListDto(likes);
    }
}
