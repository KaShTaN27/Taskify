package com.example.taskify.security;

import com.example.taskify.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = tokenProvider.resolveToken(request);
        try {
            validateTokenAndSetAuthentication(token);
        } catch (InvalidTokenException e) {
            cleanContextAndThrowException(response, e);
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(Authentication authentication) {
        if (authentication != null)
            SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void validateTokenAndSetAuthentication(String token) {
        if (token != null && tokenProvider.validateToken(token)) {
            setAuthentication(tokenProvider.getAuthentication(token));
        }
    }

    private void cleanContextAndThrowException(HttpServletResponse response, InvalidTokenException ex) {
        SecurityContextHolder.clearContext();
        response.setHeader("error", ex.getMessage());
        response.setStatus(ex.getStatus().value());
        response.setContentType("application/json");
    }
}