package com.sombra.cmsapi.apigateway.route;

import com.sombra.cmsapi.apigateway.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableHystrix
public class RouteConfiguration {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth-service/**")
                        .filters(f -> f.stripPrefix(1).filter(authenticationFilter))
                        .uri("lb://AUTH-SERVICE"))
                .route("business-service", r -> r.path("/business-service/**")
                        .filters(f -> f.stripPrefix(1).filter(authenticationFilter))
                        .uri("lb://BUSINESS-SERVICE"))
                .build();
    }

}
