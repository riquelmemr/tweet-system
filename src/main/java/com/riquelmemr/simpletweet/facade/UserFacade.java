package com.riquelmemr.simpletweet.facade;

import com.riquelmemr.simpletweet.dto.request.CreateUserRequest;
import com.riquelmemr.simpletweet.dto.request.UpdateUserRequest;
import com.riquelmemr.simpletweet.model.User;
import java.util.List;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface UserFacade {

    User register(CreateUserRequest request);

    List<User> findAll();

    User update(Long ownerId, UpdateUserRequest request, JwtAuthenticationToken token);
}
