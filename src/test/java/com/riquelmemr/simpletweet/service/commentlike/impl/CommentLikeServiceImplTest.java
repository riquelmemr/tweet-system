package com.riquelmemr.simpletweet.service.commentlike.impl;

import com.riquelmemr.simpletweet.model.Comment;
import com.riquelmemr.simpletweet.model.CommentLike;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.repository.CommentLikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceImplTest {
    @Mock
    private CommentLikeRepository commentLikeRepository;

    @InjectMocks
    private CommentLikeServiceImpl commentLikeService;

    @Test
    void shouldCreateLikeWhenNotExistingLike() {
        // Arrange
        when(commentLikeRepository.findByOwnerAndComment(any(), any())).thenReturn(Optional.empty());

        // Act
        commentLikeService.createOrDelete(new Comment(), new User());

        // Assert
        verify(commentLikeRepository, never()).delete(any());
        verify(commentLikeRepository).save(any());
    }

    @Test
    void shouldDeleteLikeWhenExistingLike() {
        // Arrange
        when(commentLikeRepository.findByOwnerAndComment(any(), any())).thenReturn(Optional.of(new CommentLike()));

        // Act
        commentLikeService.createOrDelete(new Comment(), new User());

        // Assert
        verify(commentLikeRepository).delete(any());
        verify(commentLikeRepository, never()).save(any());
    }
}