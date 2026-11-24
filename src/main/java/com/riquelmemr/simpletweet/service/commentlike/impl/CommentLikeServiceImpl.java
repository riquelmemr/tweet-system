package com.riquelmemr.simpletweet.service.commentlike.impl;

import com.riquelmemr.simpletweet.model.Comment;
import com.riquelmemr.simpletweet.model.CommentLike;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.repository.CommentLikeRepository;
import com.riquelmemr.simpletweet.service.commentlike.CommentLikeService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;

    @Override
    @Transactional
    public void createOrDelete(Comment comment, User user) {
        Optional<CommentLike> existingCommentLike = commentLikeRepository.findByOwnerAndComment(user, comment);

        if (existingCommentLike.isPresent()) {
            commentLikeRepository.delete(existingCommentLike.get());

            log.info("User [{}] removed like from comment [{}]", user.getPk(), comment.getPk());

            return;
        }

        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setOwner(user);
        commentLikeRepository.save(commentLike);

        log.info("User [{}] liked comment [{}]", user.getPk(), comment.getPk());
    }
}
