package com.riquelmemr.simpletweet.facade;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface CommentLikeFacade {

    void toggleCommentLike(Long commentId, JwtAuthenticationToken token);
}
