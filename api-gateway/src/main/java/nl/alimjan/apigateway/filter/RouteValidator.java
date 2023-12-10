package nl.alimjan.apigateway.filter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

  public static final List<String> authEndpoints = Arrays.asList(
      "/api/v1/customers/register",
      "/api/v1/customers/login",
      "/api/v1/customers/validate",
      "/eureka"
  );

  public Predicate<ServerHttpRequest> isSecured = (request) ->
      authEndpoints.stream()
          .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
