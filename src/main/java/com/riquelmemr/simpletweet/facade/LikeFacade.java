package com.riquelmemr.simpletweet.facade;

import com.riquelmemr.simpletweet.dto.response.LikeDetailResponse;
import java.util.List;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface LikeFacade {

    void createOrDelete(Long tweetId, JwtAuthenticationToken token);

    List<LikeDetailResponse> getLikesByTweet(Long tweetId, int page, int pageSize);
}
