package nl.alimjan.customer.jwt;

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


  public String generateToken(String subject) {
    return generateToken(subject, Collections.emptyMap());
  }

  public String generateToken(String subject, String... scopes) {
    return generateToken(subject, Collections.singletonMap("scopes", Arrays.asList(scopes)));
  }

  public String generateToken(String subject, List<String> scopes) {
    return generateToken(subject, Collections.singletonMap("scopes", scopes));
  }


  public String generateToken(
      String subject,
      Map<String, Object> claims) {
    return Jwts
        .builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuer("nl.alimjan")
        .setIssuedAt(Date.from(Instant.now()))
        .setExpiration(
            Date.from(
                Instant.now().plus(15, DAYS)
            )
        )
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String getSubject(String token) {
    return getClaims(token).getSubject();
  }

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
