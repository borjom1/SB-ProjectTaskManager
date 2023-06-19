package com.example.projecttaskmanager.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.info("-> doFilterInternal()");

        String authHeader = request.getHeader("Authorization");
        String token = jwtProvider.getToken(authHeader);

        if (token != null) {
            if (jwtProvider.isTokenValid(token, JwtType.ACCESS)) {
                Authentication auth = jwtProvider.getAuth(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.warn("doFilterInternal(): Invalid token");

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                try (ServletOutputStream os = response.getOutputStream()) {
                    String json = new JSONObject(Map.of("error", "invalid access token")).toString();
                    os.print(json);
                    os.flush();
                }
                return;
            }
        }

        log.info("<- doFilterInternal()");
        filterChain.doFilter(request, response);
    }

}