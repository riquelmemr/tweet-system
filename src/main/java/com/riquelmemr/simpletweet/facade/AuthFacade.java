package com.riquelmemr.simpletweet.facade;

import com.riquelmemr.simpletweet.dto.request.LoginRequest;
import com.riquelmemr.simpletweet.dto.response.LoginResponse;

public interface AuthFacade {

    LoginResponse login(LoginRequest loginRequest);
}
