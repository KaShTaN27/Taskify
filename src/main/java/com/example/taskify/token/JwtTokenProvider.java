package com.example.taskify.token;

import com.example.taskify.domain.Role;
import com.example.taskify.exception.InvalidTokenException;
import com.example.taskify.service.UserService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static io.jsonwebtoken.SignatureAlgorithm.*;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretWord;

    @Value("${jwt.expirationTime}")
    private int expirationTime;

    private final UserService userService;

    public String generateToken(String username, Collection<Role> roles) {
        return compactToken(setClaims(username, roles));
    }

    private String compactToken(Claims claims) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expirationTime);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(HS256, secretWord)
                .compact();
    }

    private Claims setClaims(String username, Collection<Role> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        List<String> roleNames = new ArrayList<>();
        roles.forEach(role -> roleNames.add(role.getName()));
        claims.put("roles", roleNames);
        return claims;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretWord).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("JWT token is invalid");
        }
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretWord).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
