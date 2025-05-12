package com.bankapp.Backend.security;

import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.security.jwt.JwtKeyProvider;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.PublicKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final JwtKeyProvider keyProvider;
    private final MyUserDetailsService userDetailsService;

    public JwtProvider(JwtKeyProvider keyProvider, MyUserDetailsService userDetailsService) {
        this.keyProvider = keyProvider;
        this.userDetailsService = userDetailsService;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(keyProvider.getPrivateKey())
                .compact();
    }


    public Authentication getAuthentication(String token) {
        PublicKey publicKey = keyProvider.getPublicKey();

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Bearer token not valid");
        }
    }


    public String getRoleFromToken(String token) {
        PublicKey publicKey = keyProvider.getPublicKey();

        return Jwts.parser()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public String getEmailFromToken(String token) {
        PublicKey publicKey = keyProvider.getPublicKey();

        return Jwts.parser()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // Correct way
    }
    public Long getIdFromToken(String token) {
        PublicKey publicKey = keyProvider.getPublicKey();

        return Jwts.parser()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);
    }


}
