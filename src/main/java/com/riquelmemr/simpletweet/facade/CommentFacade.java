package com.riquelmemr.simpletweet.facade;

import com.riquelmemr.simpletweet.dto.request.CreateCommentRequest;
import com.riquelmemr.simpletweet.dto.response.CommentResponse;
import java.util.List;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface CommentFacade {

    CommentResponse create(Long tweetId, CreateCommentRequest request, JwtAuthenticationToken token);

    CommentResponse reply(Long commentId, CreateCommentRequest request, JwtAuthenticationToken token);

    List<CommentResponse> getCommentsByTweet(Long tweetId, int page, int pageSize);
}
