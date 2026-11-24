package com.riquelmemr.simpletweet.facade.impl;

import com.riquelmemr.simpletweet.dto.request.LoginRequest;
import com.riquelmemr.simpletweet.dto.response.LoginResponse;
import com.riquelmemr.simpletweet.facade.AuthFacade;
import com.riquelmemr.simpletweet.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AuthFacadeImpl implements AuthFacade {

    private final AuthService authService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
