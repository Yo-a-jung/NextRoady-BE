package com.yoajung.jobplanner.security.firstparty.filter;

import com.yoajung.jobplanner.common.constant.Constant;
import com.yoajung.jobplanner.signin.jwt.exception.TokenNotPresentException;
import com.yoajung.jobplanner.signin.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class JwtValidationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    private String[] swagger = {
            "/v3/api-docs",
            "/swagger-ui",
            "/favicon.ico"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = jwtService.getAuthentication(resolveJwtToken(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);

    }

    private String resolveJwtToken(HttpServletRequest request){
        String bearerToken = request.getHeader(Constant.HEADER_AUTHORIZATION);
        if (bearerToken != null) {
            if (bearerToken.startsWith("Bearer")) {
                return bearerToken.substring(7);
            }
        }
        throw new TokenNotPresentException();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/user/sign-up") || path.equals("/user/sign-in") || path.equals("/user/refresh")
                || Arrays.stream(swagger).anyMatch(path::startsWith);
    }
}
