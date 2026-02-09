package com.projects.lovable_clone.security;

import com.projects.lovable_clone.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

@Component
public class AuthUtil {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    //Access token
    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES)))
                .signWith(getSecretKey())
                .compact();
    }

    public JwtUserPrinciple verifyAccessToken(String token){
        var claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = Long.parseLong(claims.get("userId", String.class));
        var username = claims.getSubject();

        return new JwtUserPrinciple(userId, username, new ArrayList<>());
    }

    public Long getCurrentUserId(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof JwtUserPrinciple userPrincipal))
            throw new AuthenticationCredentialsNotFoundException("No JWT Found");

      return userPrincipal.userId();
    }
}
