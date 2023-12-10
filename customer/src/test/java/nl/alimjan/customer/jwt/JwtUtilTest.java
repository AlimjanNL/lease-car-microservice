package nl.alimjan.customer.jwt;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

  @Test
  void generateAndValidateToken() {
    JwtUtil jwtUtil = new JwtUtil();

    String username = "testUser";
    String token = jwtUtil.generateToken(username);

    assertTrue(jwtUtil.isTokenValid(token));

    String subject = jwtUtil.getSubject(token);
    assertEquals(username, subject);
  }

  @Test
  void generateTokenWithScopes() {
    JwtUtil jwtUtil = new JwtUtil();

    String username = "testUser";
    String scope = "read";
    String token = jwtUtil.generateToken(username, scope);

    assertTrue(jwtUtil.isTokenValid(token));

    String subject = jwtUtil.getSubject(token);
    assertTrue(jwtUtil.getClaims(token).containsKey("scopes"));
    assertEquals(Collections.singletonList(scope), jwtUtil.getClaims(token).get("scopes"));
    assertEquals(username, subject);
  }

  @Test
  void isTokenExpired() throws InterruptedException {
    JwtUtil jwtUtil = new JwtUtil();

    String username = "testUser";
    String token = jwtUtil.generateToken(username);

    Thread.sleep(1);

    // Validate that the token is expired
    assertTrue(jwtUtil.isTokenValid(token));
  }
}