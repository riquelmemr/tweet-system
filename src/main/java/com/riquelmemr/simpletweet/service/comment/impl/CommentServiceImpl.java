package com.riquelmemr.simpletweet.service.comment.impl;

import com.riquelmemr.simpletweet.exceptions.EntityNotFoundException;
import com.riquelmemr.simpletweet.model.Comment;
import com.riquelmemr.simpletweet.model.Tweet;
import com.riquelmemr.simpletweet.repository.CommentRepository;
import com.riquelmemr.simpletweet.service.comment.CommentService;
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
class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void save(Comment comment) {
        commentRepository.save(comment);

        log.info("Comment [{}] saved", comment.getPk());
    }

    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id [" + id + "]"));
    }

    @Override
    public Page<Comment> findByTweet(Tweet tweet, int page, int pageSize) {
        return commentRepository.findByTweet(tweet, PageRequest.of(page, pageSize));
    }
}
