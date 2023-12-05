package com.sombra.cmsapi.apigateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://AUTH-SERVICE"))
                .build();
    }

}
