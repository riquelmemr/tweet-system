package com.riquelmemr.simpletweet.security;

import com.riquelmemr.simpletweet.config.JwtProperties;
import com.riquelmemr.simpletweet.model.Role;
import com.riquelmemr.simpletweet.model.User;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final Clock clock;

    public JwtClaimsSet generateJwtClaimSet(User user) {
        Instant now = Instant.now(clock);
        List<String> authorities = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        return JwtClaimsSet.builder()
                .issuer("simple-tweet-application")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(jwtProperties.expiration()))
                .subject(user.getPk().toString())
                .claim("authorities", authorities)
                .build();
    }

    public String generateJwtToken(User user) {
        JwtClaimsSet jwtClaimsSet = generateJwtClaimSet(user);
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    public String getIdByToken(JwtAuthenticationToken token) {
        return token.getName();
    }

    public Long getJwtExpiresIn() {
        return jwtProperties.expiration();
    }
}
