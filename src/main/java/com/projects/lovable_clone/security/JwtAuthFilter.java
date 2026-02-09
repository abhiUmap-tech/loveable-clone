package com.projects.lovable_clone.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtAuthFilter extends OncePerRequestFilter {

    AuthUtil authUtil;
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("incoming request: {}", request.getRequestURI());

        //Read Authorization Header
        final var authHeader = request.getHeader("Authorization");
        //If no header or not Bearer -> skip filter
        if (authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }

        //Extract token
        var jwtToken = authHeader.substring(7);

        //Extract username and id
        var user = authUtil.verifyAccessToken(jwtToken);

        if (user != null && SecurityContextHolder.getContext()
                .getAuthentication() == null){
            var authToken = new UsernamePasswordAuthenticationToken(
                   user, null,user.authorities());

            SecurityContextHolder.getContext()
                    .setAuthentication(authToken);
        }


        //Continue filter chain
        filterChain.doFilter(request, response);
    }
}
