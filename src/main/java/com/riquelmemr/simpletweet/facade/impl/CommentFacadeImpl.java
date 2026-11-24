package com.riquelmemr.simpletweet.facade.impl;

import com.riquelmemr.simpletweet.dto.request.CreateCommentRequest;
import com.riquelmemr.simpletweet.dto.response.CommentResponse;
import com.riquelmemr.simpletweet.facade.CommentFacade;
import com.riquelmemr.simpletweet.mapper.CommentMapper;
import com.riquelmemr.simpletweet.model.Comment;
import com.riquelmemr.simpletweet.model.Tweet;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.service.comment.CommentService;
import com.riquelmemr.simpletweet.service.tweet.TweetService;
import com.riquelmemr.simpletweet.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CommentFacadeImpl implements CommentFacade {

    private final UserService userService;
    private final TweetService tweetService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponse create(Long tweetId, CreateCommentRequest request, JwtAuthenticationToken token) {
        User user = userService.extractUserFromToken(token);
        Tweet tweet = tweetService.findById(tweetId);
        Comment comment = commentMapper.toModel(request, user, tweet);
        commentService.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    @Override
    public CommentResponse reply(Long commentId, CreateCommentRequest request, JwtAuthenticationToken token) {
        User user = userService.extractUserFromToken(token);
        Comment parentComment = commentService.findById(commentId);
        Comment reply = commentMapper.toModel(request, user, parentComment);
        commentService.save(reply);
        return commentMapper.toCommentResponse(reply);
    }

    @Override
    public List<CommentResponse> getCommentsByTweet(Long tweetId, int page, int pageSize) {
        Tweet tweet = tweetService.findById(tweetId);
        Page<Comment> comments = commentService.findByTweet(tweet, page, pageSize);
        return commentMapper.toListCommentResponse(comments);
    }
}
