package com.example.projecttaskmanager.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.example.projecttaskmanager.security.jwt.JwtType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public final class JwtProvider {

    private static final int MINUTES_VALIDITY = 60;
    private static final int DAYS_VALIDITY = 30;

    @Value("jwt.issuer")
    private String issuer;

    @Value("jwt.subject")
    private String subject;

    @Value("jwt.secret.refresh")
    private String refreshSecretKey;

    @Value("jwt.secret.access")
    private String accessSecretKey;

    private final UserDetailsService userDetailsService;

    public boolean isTokenValid(String token, JwtType type) {
        try {
            getDecodedJWT(token, type);
            return true;
        } catch (JWTVerificationException e) {
            log.error("{}", e.getMessage());
            return false;
        }
    }

    public Authentication getAuth(String accessToken) {
        String login = retrieveClaims(accessToken, ACCESS).get("login").asString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(login);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String getToken(String authHeader) {
        return authHeader == null || authHeader.length() < 7 ? null : authHeader.substring(7);
    }

    public String generateToken(JwtType type, Long id, String login) {
        var now = Instant.now();

        Instant expiration = type == REFRESH ?
                now.plus(DAYS_VALIDITY, ChronoUnit.DAYS) :
                now.plus(MINUTES_VALIDITY, ChronoUnit.MINUTES);

        return JWT.create()
                .withSubject(subject)
                .withIssuer(issuer)
                .withClaim("id", id)
                .withClaim("login", login)
                .withExpiresAt(expiration)
                .sign(Algorithm.HMAC256(type == REFRESH ? refreshSecretKey : accessSecretKey));
    }

    private Map<String, Claim> retrieveClaims(String token, JwtType type) {
        return getDecodedJWT(token, type).getClaims();
    }

    private DecodedJWT getDecodedJWT(String token, JwtType type) {
        String key = type == REFRESH ? refreshSecretKey : accessSecretKey;
        return JWT.require(Algorithm.HMAC256(key))
                .withSubject(subject)
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

}