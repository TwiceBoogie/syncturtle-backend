package dev.twiceb.common.exception;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.twiceb.common.dto.response.ApiErrorResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class GlobalReactiveExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper mapper;

    @Override
    @SuppressWarnings("null")
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.info("inside GlobalReactiveExceptionHandler");
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status;
        Object body;

        if (ex instanceof ApiRequestException are) {
            status = are.getStatus();
            body = are.getPayload() != null ? are.getPayload()
                    : Map.of("message", are.getMessage());
        } else if (ex instanceof JwtAuthenticationException jae) {
            status = jae.getHttpStatus();
            body = new ApiErrorResponse(jae.getMessage(), null);
        } else if (ex instanceof AuthException ae) {
            status = HttpStatus.UNAUTHORIZED;
            body = ae.toErrorResponse();
        } else if (ex instanceof ResponseStatusException rse) {
            status = rse.getStatusCode() instanceof HttpStatus hs ? hs
                    : HttpStatus.valueOf(rse.getStatusCode().value());
            body = Map.of("message", rse.getReason());
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            body = new ApiErrorResponse("INTERNAL SERVER ERROR", Map.of());
        }

        ServerHttpRequest req = exchange.getRequest();
        HttpMethod method = req.getMethod();
        String path = req.getURI().getRawPath();

        if (status.is4xxClientError()) {
            log.warn("Resolved exception [{}] for {} {} -> {} {}: {}",
                    ex.getClass().getSimpleName(), method, path, status.value(),
                    status.getReasonPhrase(), ex.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("Resolved stacktrace (debug only)", ex);
            }
        } else {
            log.error("Resolved exception [{}] for {} {} -> {} {}", ex.getClass().getSimpleName(),
                    method, path, status.value(), status.getReasonPhrase(), ex);
        }

        ServerHttpResponse res = exchange.getResponse();
        res.setStatusCode(status);
        res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBufferFactory bufFactory = res.bufferFactory();

        try {
            byte[] json = mapper.writeValueAsBytes(body);
            return res.writeWith(Mono.just(bufFactory.wrap(json)));
        } catch (Exception jsonError) {
            byte[] fallback =
                    "{\"message\":\"serialization error\"}".getBytes(StandardCharsets.UTF_8);
            return res.writeWith(Mono.just(bufFactory.wrap(fallback)));
        }
    }

}
