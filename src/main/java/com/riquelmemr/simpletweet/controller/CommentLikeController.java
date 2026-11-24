package com.riquelmemr.simpletweet.controller;

import com.riquelmemr.simpletweet.facade.CommentLikeFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tweets/comments")
@RequiredArgsConstructor
public class CommentLikeController extends BaseController {

    private final CommentLikeFacade commentLikeFacade;

    @PostMapping("/{commentId}/likes")
    public ResponseEntity<Void> toggleCommentLike(
            @PathVariable final Long commentId,
            JwtAuthenticationToken jwtAuthenticationToken
    ) {
        commentLikeFacade.toggleCommentLike(commentId, jwtAuthenticationToken);
        return handleResponse(HttpStatus.OK, null);
    }
}
