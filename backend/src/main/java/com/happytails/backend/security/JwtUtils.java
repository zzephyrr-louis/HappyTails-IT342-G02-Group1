package com.happytails.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    private final String jwtSecret = "BzTqLiYjD74ipte3GXBnS1i8t9amcRpr2mAlDQowmWb/pKBRlg2bv+eZDL5rsbghfH13e88hiKnmPtCUky6htA==";
    private final int jwtExpirationMs = 86400000; // 24 hours

    // Generate Token
    public String generateJwtToken(String email, List<String> roles) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Get Username from Token
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Get Roles from Token
    public List<String> getRolesFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Object roles = claims.get("roles");
            if (roles instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<String> cast = (List<String>) roles;
                return cast;
            }
        } catch (JwtException e) {
            System.err.println("Failed to parse roles from JWT: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    // Validate Token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}