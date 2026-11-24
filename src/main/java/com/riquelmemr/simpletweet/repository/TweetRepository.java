package com.riquelmemr.simpletweet.repository;

import com.riquelmemr.simpletweet.model.Tweet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    List<Tweet> findByAuthorPk(Long authorId);
}
