package com.riquelmemr.simpletweet.controller;

import com.riquelmemr.simpletweet.dto.request.CreateCommentRequest;
import com.riquelmemr.simpletweet.dto.response.CommentResponse;
import com.riquelmemr.simpletweet.facade.CommentFacade;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tweets/")
@RequiredArgsConstructor
public class CommentController extends BaseController {

    private final CommentFacade commentFacade;

    @PostMapping("/{tweetId}/comment")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable final Long tweetId,
            @Valid @RequestBody final CreateCommentRequest dto,
            JwtAuthenticationToken token
    ) {
        CommentResponse comment = commentFacade.create(tweetId, dto, token);
        return handleResponse(HttpStatus.OK, comment);
    }

    @PostMapping("/comments/{commentId}/reply")
    public ResponseEntity<CommentResponse> replyComment(
            @PathVariable final Long commentId,
            @Valid @RequestBody final CreateCommentRequest dto,
            JwtAuthenticationToken token
    ) {
        CommentResponse reply = commentFacade.reply(commentId, dto, token);
        return handleResponse(HttpStatus.OK, reply);
    }

    @GetMapping("/{tweetId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByTweet(
            @PathVariable final Long tweetId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "50") int pageSize
    ) {
        List<CommentResponse> commentResponses = commentFacade.getCommentsByTweet(tweetId, page, pageSize);
        return handleResponse(HttpStatus.OK, commentResponses);
    }
}
