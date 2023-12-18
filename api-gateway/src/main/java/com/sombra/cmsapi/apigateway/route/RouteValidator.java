package com.sombra.cmsapi.apigateway.route;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class RouteValidator {
  public static final List<String> whitelistedEndpionts = List.of("/auth", "/users/register");

  public Predicate<ServerHttpRequest> isSecured =
      serverHttpRequest ->
          whitelistedEndpionts.stream()
              .noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));
}
