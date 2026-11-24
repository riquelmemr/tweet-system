package com.riquelmemr.simpletweet.service.auth.impl;

import com.riquelmemr.simpletweet.builder.UserBuilder;
import com.riquelmemr.simpletweet.dto.request.LoginRequest;
import com.riquelmemr.simpletweet.dto.response.LoginResponse;
import com.riquelmemr.simpletweet.mapper.UserMapper;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.repository.UserRepository;
import com.riquelmemr.simpletweet.security.JwtUtils;
import com.riquelmemr.simpletweet.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldReturnLoginResponseWhenLoginRequestIsValid() {
        // Arrange
        String token = "12345abcde";

        User user = new UserBuilder()
                .withUsername("test")
                .withPk(1L)
                .withPassword("test123")
                .build();

        LoginRequest loginRequest = new LoginRequest("test", "test123");
        LoginResponse loginResponse = new LoginResponse(token, jwtUtils.getJwtExpiresIn());

        when(userService.findByUsername("test")).thenReturn(user);
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(true);
        when(jwtUtils.generateJwtToken(user)).thenReturn(token);
        when(userMapper.toLoginResponseDto(token, jwtUtils.getJwtExpiresIn())).thenReturn(loginResponse);

        // Act
        LoginResponse result = authService.login(loginRequest);

        // Assert
        assertNotNull(result.accessToken());
        assertEquals(token, result.accessToken());
    }

    @Test
    void shouldReturnLoginResponseWhenUserNotExists() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test", "test123");
        when(userService.findByUsername("test")).thenReturn(null);

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtils, never()).generateJwtToken(any());

        assertEquals("Username or password is invalid.", exception.getMessage());
    }

    @Test
    void shouldReturnLoginResponseWhenPasswordDoesNotMatch() {
        // Arrange
        User user = new UserBuilder()
                .withUsername("test")
                .withPk(1L)
                .withPassword("valid-password")
                .build();

        LoginRequest loginRequest = new LoginRequest("test", "invalid-password");
        when(userService.findByUsername("test")).thenReturn(user);

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));

        verify(passwordEncoder).matches(any(), any());
        verify(jwtUtils, never()).generateJwtToken(any());

        assertEquals("Username or password is invalid.", exception.getMessage());
    }
}
