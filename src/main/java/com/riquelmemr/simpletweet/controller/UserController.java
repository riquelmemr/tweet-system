package com.riquelmemr.simpletweet.controller;

import com.riquelmemr.simpletweet.dto.request.CreateUserRequest;
import com.riquelmemr.simpletweet.dto.request.UpdateUserRequest;
import com.riquelmemr.simpletweet.dto.response.UserResponse;
import com.riquelmemr.simpletweet.facade.UserFacade;
import com.riquelmemr.simpletweet.mapper.UserMapper;
import com.riquelmemr.simpletweet.model.User;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserFacade userFacade;
    private final UserMapper userMapper;

    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest dto) {
        User userCreated = userFacade.register(dto);
        return handleResponse(HttpStatus.CREATED, userMapper.toUserResponseDto(userCreated));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAllUsers() {
        List<User> users = userFacade.findAll();
        return handleResponse(HttpStatus.OK, userMapper.toListUserResponseDto(users));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            JwtAuthenticationToken token
    ) {
        User user = userFacade.update(id, request, token);
        return handleResponse(HttpStatus.OK, userMapper.toUserResponseDto(user));
    }
}
