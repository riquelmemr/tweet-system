package com.riquelmemr.simpletweet.service.auth.impl;

import com.riquelmemr.simpletweet.dto.request.LoginRequest;
import com.riquelmemr.simpletweet.dto.response.LoginResponse;
import com.riquelmemr.simpletweet.mapper.UserMapper;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.security.JwtUtils;
import com.riquelmemr.simpletweet.service.auth.AuthService;
import com.riquelmemr.simpletweet.service.user.UserService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class AuthServiceImpl implements AuthService {

    private static final String BAD_CREDENTIALS_ERROR_MESSAGE = "Username or password is invalid.";

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.username());

        if (Objects.isNull(user)) {
            log.warn("Login failed: user [{}] not found", loginRequest.username());
            throw new BadCredentialsException(BAD_CREDENTIALS_ERROR_MESSAGE);
        }

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            log.warn("Login failed: invalid password for user [{}]", user.getPk());
            throw new BadCredentialsException(BAD_CREDENTIALS_ERROR_MESSAGE);
        }

        String token = jwtUtils.generateJwtToken(user);

        log.info("User [{}] logged in", user.getPk());

        return userMapper.toLoginResponseDto(token, jwtUtils.getJwtExpiresIn());
    }
}
