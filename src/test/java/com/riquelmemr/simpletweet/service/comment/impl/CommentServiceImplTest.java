package com.riquelmemr.simpletweet.service.comment.impl;

import com.riquelmemr.simpletweet.builder.CommentBuilder;
import com.riquelmemr.simpletweet.builder.TweetBuilder;
import com.riquelmemr.simpletweet.exceptions.EntityNotFoundException;
import com.riquelmemr.simpletweet.model.Comment;
import com.riquelmemr.simpletweet.model.Tweet;
import com.riquelmemr.simpletweet.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;
    private Tweet tweet;

    @BeforeEach
    void setUp() {
        tweet = new TweetBuilder().random().build();
        comment = new CommentBuilder()
                .withPk(1L)
                .withContent("test content comment")
                .build();
    }

    @Test
    void shouldSaveCommentSuccessfully() {
        // Arrange
        when(commentRepository.save(comment)).thenReturn(comment);

        // Act
        commentService.save(comment);

        // Assert
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void shouldFindCommentByIdSuccessfully() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act
        Comment result = commentService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("test content comment", result.getContent());
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCommentNotFound() {
        // Arrange
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.findById(99L));

        assertEquals("Comment not found with id [99]", exception.getMessage());
        verify(commentRepository, times(1)).findById(99L);
    }

    @Test
    void shouldFindCommentsByTweetSuccessfully() {
        // Arrange
        Page<Comment> page = new PageImpl<>(List.of(comment));
        when(commentRepository.findByTweet(tweet, PageRequest.of(0, 10))).thenReturn(page);

        // Act
        Page<Comment> result = commentService.findByTweet(tweet, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("test content comment", result.getContent().get(0).getContent());
        verify(commentRepository, times(1)).findByTweet(tweet, PageRequest.of(0, 10));
    }
}
