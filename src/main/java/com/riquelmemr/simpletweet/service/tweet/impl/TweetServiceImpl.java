package com.riquelmemr.simpletweet.service.tweet.impl;

import com.riquelmemr.simpletweet.dto.request.UpdateTweetRequest;
import com.riquelmemr.simpletweet.enums.RoleEnum;
import com.riquelmemr.simpletweet.exceptions.EntityNotFoundException;
import com.riquelmemr.simpletweet.exceptions.ResourceNotAllowedException;
import com.riquelmemr.simpletweet.model.Tweet;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.repository.TweetRepository;
import com.riquelmemr.simpletweet.service.tweet.TweetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;

    @Override
    @Transactional
    public void create(Tweet tweet) {
        tweetRepository.save(tweet);

        log.info("Tweet [{}] created by user [{}]", tweet.getPk(), tweet.getAuthor().getPk());
    }

    @Override
    @Transactional
    public Tweet update(Long id, UpdateTweetRequest request, User user) {
        Tweet tweet = findById(id);
        validatePermission(user, tweet, "update");
        updateTweetContent(request, tweet);
        tweetRepository.save(tweet);

        log.info("Tweet [{}] updated by user [{}]", id, user.getPk());

        return tweet;
    }

    @Override
    @Transactional
    public void deleteById(Long id, User user) {
        Tweet tweet = findById(id);
        validatePermission(user, tweet, "delete");
        tweetRepository.deleteById(id);

        log.info("Tweet [{}] deleted by user [{}]", id, user.getPk());
    }

    @Override
    public Tweet findById(Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tweet not found with id [" + id + "]"));
    }

    @Override
    public List<Tweet> findByUserId(Long userId) {
        return tweetRepository.findByAuthorPk(userId);
    }

    @Override
    public Page<Tweet> findAll(int page, int pageSize) {
        return tweetRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTime"));
    }

    private void validatePermission(User user, Tweet tweet, String action) {
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN.name()));

        boolean isOwner = tweet.getAuthor().getPk().equals(user.getPk());

        if (!isAdmin && !isOwner) {
            log.warn("User [{}] is not allowed to [{}] tweet [{}]", user.getPk(), action, tweet.getPk());
            throw new ResourceNotAllowedException("You do not have permission to " + action + " this tweet.");
        }
    }

    private void updateTweetContent(UpdateTweetRequest request, Tweet tweet) {
        tweet.setContent(request.content());
    }
}
