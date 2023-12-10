package nl.alimjan.apigateway.jwt;

import static java.time.temporal.ChronoUnit.DAYS;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {

  private static final String SECRET_KEY =
      "long_long_secret_key_123456789_long_long_secret_key_123456789";

  public Claims getClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
  }

  public boolean isTokenValid(String jwt) {
    return !isTokenExpired(jwt);
  }

  private boolean isTokenExpired(String jwt) {
    Date today = Date.from(Instant.now());
    return getClaims(jwt).getExpiration().before(today);
  }
}
