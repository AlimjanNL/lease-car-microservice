package nl.alimjan.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import nl.alimjan.apigateway.exception.AuthException;
import nl.alimjan.apigateway.jwt.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

  private final RouteValidator routeValidator;
  private final JwtUtil jwtUtil;
//  private RestTemplate restTemplate;

  public AuthFilter(RouteValidator routeValidator, JwtUtil jwtUtil) {
    super(Config.class);
    this.routeValidator = routeValidator;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return ((exchange, chain) -> {

      if (routeValidator.isSecured.test(exchange.getRequest())) {
        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
          throw new AuthException("missing authorization header");
        }

        String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)
            .get(0);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
          authHeader = authHeader.substring(7);
        }

        try {
//          restTemplate.getForObject("http://CUSTOMER//validate?token=" + authHeader, String.class);

          if (!jwtUtil.isTokenValid(authHeader)) {
            log.info("invalid token");
            throw new AuthException("invalid token");
          }

        } catch (Exception e) {
          log.info("invalid token");
          throw new AuthException("invalid token");
        }

      }

      return chain.filter(exchange);
    });
  }

  public static class Config {

  }
}
