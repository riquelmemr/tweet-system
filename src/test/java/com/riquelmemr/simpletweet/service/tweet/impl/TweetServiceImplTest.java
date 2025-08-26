package com.riquelmemr.simpletweet.service.tweet.impl;

import com.riquelmemr.simpletweet.dto.request.UpdateTweetRequest;
import com.riquelmemr.simpletweet.enums.RoleEnum;
import com.riquelmemr.simpletweet.exceptions.EntityNotFoundException;
import com.riquelmemr.simpletweet.exceptions.ResourceNotAllowedException;
import com.riquelmemr.simpletweet.model.Role;
import com.riquelmemr.simpletweet.model.Tweet;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.repository.TweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TweetServiceImplTest {
    @Mock
    private TweetRepository tweetRepository;

    @InjectMocks
    private TweetServiceImpl tweetService;

    private User user;
    private User requester;
    private Role basicRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setPk(1L);
        user.setUsername("john");
        user.setPassword("123456");

        requester = new User();
        requester.setPk(2L);
        requester.setUsername("admin requester");

        basicRole = new Role();
        basicRole.setName(RoleEnum.BASIC.name().toUpperCase());
        adminRole = new Role();
        adminRole.setName(RoleEnum.ADMIN.name().toUpperCase());
    }

    @Test
    void shouldCreateTweetSuccessfully() {
        // Arrange
        Tweet tweet = getTweetMock(user);

        // Act
        tweetService.create(tweet);

        // Assert
        verify(tweetRepository, times(1)).save(tweet);
    }

    @Test
    void shouldAllowOwnerToUpdateHimself() {
        // Arrange
        user.setRoles(Set.of(basicRole));
        Tweet tweet = getTweetMock(user);

        UpdateTweetRequest request = new UpdateTweetRequest("Test content updated");

        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        // Act
        tweetService.update(1L, request, user);

        // Assert
        verify(tweetRepository).save(tweet);
        assertEquals(tweet.getContent(), request.content());
    }

    @Test
    void shouldAllowAdminToUpdate() {
        // Arrange
        requester.setRoles(Set.of(adminRole));
        Tweet tweet = getTweetMock(user);

        UpdateTweetRequest request = new UpdateTweetRequest("Test content updated");

        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        // Act
        tweetService.update(1L, request, requester);

        // Assert
        verify(tweetRepository).save(tweet);
        assertEquals(tweet.getContent(), request.content());
    }

    @Test
    void shouldThrowExceptionWhenUnauthorizedUserTriesToUpdate() {
        // Arrange
        requester.setRoles(Set.of(adminRole));

        UpdateTweetRequest request = new UpdateTweetRequest("Test content updated");
        Tweet tweet = getTweetMock(user);

        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        // Act
        tweetService.update(1L, request, requester);

        // Assert
        verify(tweetRepository).save(tweet);
        assertEquals(tweet.getContent(), request.content());
    }

    @Test
    void shouldThrowExceptionsWhenTweetNotFound() {
        // Arrange
        user.setRoles(Set.of(basicRole));

        UpdateTweetRequest request = new UpdateTweetRequest("Test content updated");
        when(tweetRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> tweetService.update(2L, request, user));

        verify(tweetRepository, never()).save(any());
    }

    @Test
    void shouldDeleteTweetWhenUserHasPermission() {
        // Arrange
        user.setRoles(Set.of(basicRole));
        Tweet tweet = getTweetMock(user);
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        // Act
        tweetService.deleteById(1L, user);

        // Assert
        verify(tweetRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserHasNoPermissionToDelete() {
        // Arrange
        requester.setRoles(Set.of(basicRole));
        Tweet tweet = getTweetMock(user);

        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        // Act & Assert
        assertThrows(ResourceNotAllowedException.class, () -> tweetService.deleteById(1L, requester));
        verify(tweetRepository, never()).deleteById(any());
    }

    @Test
    void shouldFindTweetByIdWhenExists() {
        // Arrange
        Tweet tweet = getTweetMock(user);

        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        // Act
        Tweet found = tweetService.findById(1L);

        // Assert
        assertNotNull(found);
        assertEquals(1L, found.getPk());
    }

    @Test
    void shouldThrowExceptionWhenTweetNotFoundById() {
        // Arrange
        when(tweetRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> tweetService.findById(99L));

        assertEquals("Tweet not found with id [99]", ex.getMessage());
    }

    @Test
    void shouldReturnTweetsWhenUserExists() {
        // Arrange
        Long userId = 1L;
        List<Tweet> tweets = Arrays.asList(new Tweet(), new Tweet());
        when(tweetRepository.findByAuthorPk(userId)).thenReturn(tweets);

        // Act
        List<Tweet> result = tweetService.findByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tweetRepository).findByAuthorPk(userId);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoTweets() {
        // arrange
        Long userId = 1L;
        when(tweetRepository.findByAuthorPk(userId)).thenReturn(Collections.emptyList());

        // act
        List<Tweet> result = tweetService.findByUserId(userId);

        // assert
        assertTrue(result.isEmpty());
        verify(tweetRepository).findByAuthorPk(userId);
    }

    @Test
    void shouldReturnPageOfTweetsWhenPageRequestIsValid() {
        // Arrange
        int page = 0;
        int pageSize = 10;

        Page<Tweet> pageResult = new PageImpl<>(Arrays.asList(new Tweet(), new Tweet()));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTime");

        when(tweetRepository.findAll(pageable))
                .thenReturn(pageResult);

        // Act
        Page<Tweet> result = tweetService.findAll(page, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(tweetRepository).findAll(pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenNoTweetExists() {
        // Arrange
        when(tweetRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        // Act
        Page<Tweet> result = tweetService.findAll(0, 10);

        // Assert
        assertTrue(result.isEmpty());
        verify(tweetRepository).findAll(any(Pageable.class));
    }

    private Tweet getTweetMock(User user) {
        Tweet tweet = new Tweet();
        tweet.setPk(1L);
        tweet.setContent("Test content");
        tweet.setAuthor(user);
        return tweet;
    }
}
