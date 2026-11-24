package com.riquelmemr.simpletweet.builder;

import com.riquelmemr.simpletweet.model.Comment;
import com.riquelmemr.simpletweet.model.Like;
import com.riquelmemr.simpletweet.model.Tweet;
import com.riquelmemr.simpletweet.model.User;

public class TweetBuilder {
    private final Tweet tweet;

    public TweetBuilder() {
        tweet = new Tweet();
    }

    public TweetBuilder withPk(Long pk) {
        tweet.setPk(pk);
        return this;
    }

    public TweetBuilder withContent(String content) {
        tweet.setContent(content);
        return this;
    }

    public TweetBuilder withAuthor(User author) {
        tweet.setAuthor(author);
        return this;
    }

    public TweetBuilder addLike(Like like) {
        tweet.getLikes().add(like);
        return this;
    }

    public TweetBuilder addComment(Comment comment) {
        tweet.getComments().add(comment);
        return this;
    }

    public TweetBuilder random() {
        User user = new UserBuilder().random().build();

        return this.withPk(1L)
                .withAuthor(user)
                .withContent("test content");
    }

    public Tweet build() {
        return tweet;
    }
}
