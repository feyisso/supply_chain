package com.awashbank.supply_chain.jwt;

import com.awashbank.supply_chain.user.model.UserDetailModel;
import com.awashbank.supply_chain.user.model.UserDetailRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Date;

@Component
public class TokenManager implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private static final long serialVersionUID = 7008375124389347049L;
    public static final long TOKEN_VALIDITY = 10 * 60 * 60;
    @Value("${secret}")
    private String SECRET_KEY;

    @Autowired
    UserDetailRepository usr;

    public String generateToken(String username) {
        UserDetailModel uss = usr.byUser(username);

        return Jwts.builder()
                .subject(username)
                .id(uss.getId().toString())
                .issuer(uss.getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY + 1000 )) // 1-hour expiry
                .signWith(getSigningKey(), Jwts.SIG.HS256) // ✅ Correct signing method
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // ✅ Use `verifyWith()`
                .build()
                .parseSignedClaims(token) // ✅ Correct parsing method
                .getPayload()
                .getSubject();
    }

    public String extractUserId(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // ✅ Use `verifyWith()`
                .build()
                .parseSignedClaims(token) // ✅ Correct parsing method
                .getPayload()
                .getId();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false; // Invalid token
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(getSigningKey()) // ✅ Use `verifyWith()`
                    .build()
                    .parseSignedClaims(token) // ✅ Correct parsing method
                    .getPayload()
                    .getExpiration();
            return expiration.before(new Date()); // ✅ Check expiry
        } catch (ExpiredJwtException e) {
            return true; // Token is expired
        } catch (Exception e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
