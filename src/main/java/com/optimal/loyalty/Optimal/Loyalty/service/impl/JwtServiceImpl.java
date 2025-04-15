package com.optimal.loyalty.Optimal.Loyalty.service.impl;

import com.optimal.loyalty.Optimal.Loyalty.service.JwtService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${privateKey}")
    private String secretKey;

    @Value("${jwtExpirationTime}")
    private long jwtExpirationTime;


    @Override
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(getSigninKey())
                .compact();

    }

    @Override
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        return userDetails.getUsername().equals(extractUsername(token))&&!isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }


    private SecretKey getSigninKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
