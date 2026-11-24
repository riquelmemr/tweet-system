package com.riquelmemr.simpletweet.builder;

import com.riquelmemr.simpletweet.model.Comment;
import com.riquelmemr.simpletweet.model.CommentLike;
import com.riquelmemr.simpletweet.model.Tweet;
import com.riquelmemr.simpletweet.model.User;
import java.util.Set;

public class CommentBuilder {

    private final Comment comment;

    public CommentBuilder() {
        comment = new Comment();
    }

    public CommentBuilder withPk(Long pk) {
        comment.setPk(pk);
        return this;
    }

    public CommentBuilder withContent(String content) {
        comment.setContent(content);
        return this;
    }

    public CommentBuilder withTweet(Tweet tweet) {
        comment.setTweet(tweet);
        return this;
    }

    public CommentBuilder withOwner(User owner) {
        comment.setOwner(owner);
        return this;
    }

    public CommentBuilder withParentComment(Comment parentComment) {
        comment.setParentComment(parentComment);
        return this;
    }

    public CommentBuilder withReplies(Set<Comment> replies) {
        comment.setReplies(replies);
        return this;
    }

    public CommentBuilder withLikes(Set<CommentLike> likes) {
        comment.setLikes(likes);
        return this;
    }

    public Comment build() {
        return comment;
    }
}
