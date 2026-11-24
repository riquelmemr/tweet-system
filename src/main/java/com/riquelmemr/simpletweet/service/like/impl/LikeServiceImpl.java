package com.riquelmemr.simpletweet.service.like.impl;

import com.riquelmemr.simpletweet.model.Like;
import com.riquelmemr.simpletweet.model.Tweet;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.repository.LikeRepository;
import com.riquelmemr.simpletweet.service.like.LikeService;
import com.riquelmemr.simpletweet.service.tweet.TweetService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class LikeServiceImpl implements LikeService {

    private final TweetService tweetService;
    private final LikeRepository likeRepository;

    @Override
    @Transactional
    public void createOrDelete(Long tweetId, User user) {
        Tweet tweet = tweetService.findById(tweetId);

        Optional<Like> existingLike = likeRepository.findByOwnerAndTweet(user, tweet);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());

            log.info("User [{}] removed like from tweet [{}]", user.getPk(), tweetId);

            return;
        }

        Like like = new Like();
        like.setOwner(user);
        like.setTweet(tweet);
        likeRepository.save(like);

        log.info("User [{}] liked tweet [{}]", user.getPk(), tweetId);
    }

    @Override
    public long countLikesForTweet(Tweet tweet) {
        return likeRepository.countByTweet(tweet);
    }

    @Override
    public Page<Like> findByTweet(Long tweetId, int page, int pageSize) {
        Tweet tweet = tweetService.findById(tweetId);
        return likeRepository.findByTweet(tweet, PageRequest.of(page, pageSize));
    }
}
