// package dev.twiceb.apigateway.filter;

// import dev.twiceb.apigateway.service.UserService;
// import dev.twiceb.common.exception.JwtAuthenticationException;
// import dev.twiceb.common.security.JwtProvider;
// import lombok.extern.slf4j.Slf4j;
// import reactor.core.publisher.Mono;
// import org.springframework.cloud.gateway.filter.GatewayFilter;
// import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
// import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.stereotype.Component;

// import static dev.twiceb.common.constants.ErrorMessage.JWT_TOKEN_EXPIRED;
// import static dev.twiceb.common.constants.PathConstants.*;
// import java.util.UUID;

// @Slf4j
// @Component
// public class AuthGatewayFilterFactory
// extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.Config> {

// private final JwtProvider jwtProvider;
// private final UserService userService;

// public AuthGatewayFilterFactory(JwtProvider jwtProvider, UserService userService) {
// super(Config.class);
// this.jwtProvider = jwtProvider;
// this.userService = userService;
// }

// @Override
// public GatewayFilter apply(Config config) {
// return ((exchange, chain) -> {
// log.info("==> Inside Auth filter <==");
// String token = jwtProvider.resolveToken(exchange.getRequest());
// if (token == null || !jwtProvider.validateToken(token, "main")) {
// return Mono.error(new JwtAuthenticationException(JWT_TOKEN_EXPIRED));
// }

// String userId = jwtProvider.parseToken(token);
// return userService.getCachedUserDetails(UUID.fromString(userId)).flatMap(user -> {
// // store id in exchange for downsteram filters or hadnlers
// exchange.getAttributes().put("userId", userId);

// ServerHttpRequest mutated = exchange.getRequest().mutate()
// .header(AUTH_USER_ID_HEADER, user.getId().toString()).build();

// return chain.filter(exchange.mutate().request(mutated).build());
// }).onErrorMap(ex -> {
// return (ex instanceof JwtAuthenticationException) ? ex
// : new JwtAuthenticationException(JWT_TOKEN_EXPIRED);
// });
// });
// }

// public static class Config {
// }
// }
