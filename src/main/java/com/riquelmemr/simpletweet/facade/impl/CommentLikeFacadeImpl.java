package com.riquelmemr.simpletweet.facade.impl;

import com.riquelmemr.simpletweet.facade.CommentLikeFacade;
import com.riquelmemr.simpletweet.model.Comment;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.service.comment.CommentService;
import com.riquelmemr.simpletweet.service.commentlike.CommentLikeService;
import com.riquelmemr.simpletweet.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CommentLikeFacadeImpl implements CommentLikeFacade {

    private final UserService userService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    @Override
    public void toggleCommentLike(Long commentId, JwtAuthenticationToken token) {
        User user = userService.extractUserFromToken(token);
        Comment comment = commentService.findById(commentId);
        commentLikeService.createOrDelete(comment, user);
    }
}
