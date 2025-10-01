package dev.twiceb.apigateway.filter;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import dev.twiceb.apigateway.service.util.HmacCsrfToken;
import dev.twiceb.common.dto.response.AuthErrorResponse;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.spring.WebfluxHostResolverAdapterAutoConfiguration.WebfluxHostResolverAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsrfValidationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<CsrfValidationGatewayFilterFactory.Config> {

    private static final String CSRF_TOKEN = "csrftoken";
    private static final String CSRF_FORM_FIELD = "csrf_token";
    private static final String NEXT_PATH_FIELD = "next_path";

    private final WebfluxHostResolverAdapter resolverAdapter;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (isSafeMethod(exchange.getRequest().getMethod())) {
                return chain.filter(exchange); // skip csrf check on safe method
            }

            MediaType contentType = exchange.getRequest().getHeaders().getContentType();
            if (contentType != null
                    && MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
                return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    String formData = new String(bytes, StandardCharsets.UTF_8);

                    Map<String, String> formMap = parseFormData(formData);
                    String submittedToken = formMap.get(CSRF_FORM_FIELD);
                    String nextPath = formMap.get(NEXT_PATH_FIELD);
                    String cookieToken = getCookie(exchange, CSRF_TOKEN);

                    if (submittedToken == null || cookieToken == null
                            || !HmacCsrfToken.isValid(submittedToken)
                            || !submittedToken.split("\\.")[0].equals(cookieToken)) {
                        log.warn("CSRF validation failed");
                        AuthException exception =
                                new AuthException(AuthErrorCodes.AUTHENTICATION_FAILED);
                        AuthErrorResponse errorResponse = exception.toErrorResponse();
                        String baseHost = resolverAdapter.resolve(exchange.getRequest(), false,
                                false, true, null);
                        String cleanNextPath = resolverAdapter.validateNextPath(nextPath);

                        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseHost)
                                .queryParam("error_code", errorResponse.getErrorCode())
                                .queryParam("error_message", errorResponse.getErrorMessage());

                        if (!cleanNextPath.isBlank()) {
                            builder.queryParam("next_path", cleanNextPath);
                        }

                        String redirectUrl = builder.toUriString();

                        exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER); // 303 redirect
                        exchange.getRequest().getHeaders().setLocation(URI.create(redirectUrl));
                        return exchange.getResponse().setComplete();
                    }

                    // remove csrk input field
                    formMap.remove(CSRF_FORM_FIELD);
                    String newBody = buildFormBody(formMap);

                    ServerHttpRequestDecorator decorator =
                            new ServerHttpRequestDecorator(exchange.getRequest()) {
                                @NonNull
                                @Override
                                public HttpHeaders getHeaders() {
                                    HttpHeaders headers = new HttpHeaders();
                                    headers.putAll(super.getHeaders());
                                    headers.setContentLength(
                                            newBody.getBytes(StandardCharsets.UTF_8).length);
                                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                    return headers;
                                }

                                @NonNull
                                @Override
                                public Flux<DataBuffer> getBody() {
                                    DataBuffer buffer = exchange.getResponse().bufferFactory()
                                            .wrap(newBody.getBytes(StandardCharsets.UTF_8));
                                    return Flux.just(buffer);
                                }
                            };

                    return chain.filter(exchange.mutate().request(decorator).build());

                });
            }
            return chain.filter(exchange);
        };
    }

    private boolean isSafeMethod(HttpMethod method) {
        return method == null || method == HttpMethod.GET || method == HttpMethod.HEAD
                || method == HttpMethod.OPTIONS;
    }

    private Map<String, String> parseFormData(String data) {
        return Arrays.stream(data.split("&")).map(kv -> kv.split("=", 2))
                .filter(pair -> pair.length == 2)
                .collect(Collectors.toMap(kv -> URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        kv -> URLDecoder.decode(kv[1], StandardCharsets.UTF_8), (a, b) -> b));
    }

    private String buildFormBody(Map<String, String> formMap) {
        return formMap.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    private String getCookie(ServerWebExchange exchange, String csrfToken) {
        return Optional.ofNullable(exchange.getRequest().getCookies().getFirst(csrfToken))
                .map(cookie -> URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8))
                .orElse(null);
    }

    public static class Config {
    }

}
