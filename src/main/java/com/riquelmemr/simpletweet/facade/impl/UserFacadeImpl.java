package com.riquelmemr.simpletweet.facade.impl;

import com.riquelmemr.simpletweet.dto.request.CreateUserRequest;
import com.riquelmemr.simpletweet.dto.request.UpdateUserRequest;
import com.riquelmemr.simpletweet.facade.UserFacade;
import com.riquelmemr.simpletweet.mapper.UserMapper;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public User register(CreateUserRequest request) {
        User user = userMapper.toModel(request);
        userService.create(user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return userService.findAll();
    }

    @Override
    public User update(Long ownerId, UpdateUserRequest request, JwtAuthenticationToken token) {
        return userService.update(ownerId, request, token);
    }
}
