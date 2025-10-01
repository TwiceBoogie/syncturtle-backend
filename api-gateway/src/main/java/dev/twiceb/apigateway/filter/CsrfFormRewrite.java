package dev.twiceb.apigateway.filter;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.twiceb.apigateway.service.util.HmacCsrfToken;
import dev.twiceb.common.dto.response.AuthErrorResponse;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.spring.WebfluxHostResolverAdapterAutoConfiguration.WebfluxHostResolverAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsrfFormRewrite implements RewriteFunction<String, String> {

    private static final String CSRF_COOKIE = "csrftoken";
    private static final String CSRF_FIELD = "csrf_token";
    private static final String NEXT_PATH = "next_path";

    private final WebfluxHostResolverAdapter resolverAdapter;
    private final ObjectMapper mapper;

    @Override
    public Mono<String> apply(ServerWebExchange exchange, String body) {
        // only for form posts
        log.info("===> CsrfFormRewrite <===");
        MediaType contentType = exchange.getRequest().getHeaders().getContentType();

        if (contentType != null) {
            log.info("CT full={}, type={}, subtype={}", contentType.toString(), // e.g.
                                                                                // application/x-www-form-urlencoded
                    contentType.getType(), // application
                    contentType.getSubtype()); // x-www-form-urlencoded
        }

        if (contentType != null && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            return Mono.justOrEmpty(body);
        }
        if (contentType != null
                && !MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
            return Mono.justOrEmpty(body);
        }

        log.info(body);
        Map<String, String> form = parseForm(body);
        log.info("Form", form);
        String submittedToken = form.get(CSRF_FIELD);
        String nextPath = form.get(NEXT_PATH);
        String cookieToken = getCookie(exchange, CSRF_COOKIE);

        boolean ok = submittedToken != null && cookieToken != null
                && submittedToken.equals(cookieToken) && HmacCsrfToken.isValid(submittedToken);

        if (!ok) {
            // buuild redirect for failure (303 + location) and short-cricut the chian
            log.warn("CSRF validation failed");
            AuthException exception = new AuthException(AuthErrorCodes.AUTHENTICATION_FAILED);
            AuthErrorResponse errorResponse = exception.toErrorResponse();
            String base = resolverAdapter.resolve(exchange.getRequest(), false, false, true, null);
            String cleanNextPath = resolverAdapter.validateNextPath(nextPath);
            URI location = UriComponentsBuilder.fromUriString(base)
                    .queryParam("error_code", errorResponse.getErrorCode())
                    .queryParam("error_message", errorResponse.getErrorMessage())
                    .queryParamIfPresent("next_path",
                            Optional.ofNullable(cleanNextPath).filter(s -> !s.isBlank()))
                    .build(true).toUri();

            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().setLocation(location);
            // returning an error mono stops routing to downstream services
            return Mono.error(new ResponseStatusException(HttpStatus.SEE_OTHER));
        }

        form.remove(CSRF_FIELD);

        // tell downsteram we're sending json and let scg recompute length
        // exchange.getRequest().mutate().headers(h -> {
        // h.setContentType(MediaType.APPLICATION_JSON);
        // h.remove(HttpHeaders.CONTENT_LENGTH);
        // }).build();

        try {
            String json = mapper.writeValueAsString(form);
            return Mono.just(json);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private Map<String, String> parseForm(String body) {
        Map<String, String> formMap = new LinkedHashMap<>(); // preserve order

        if (body == null || body.isBlank())
            return new HashMap<>();

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);

            if (kv.length != 2)
                continue;

            formMap.put(urlDecode(kv[0]), urlDecode(kv[1]));
        }

        return formMap;
    }

    private String urlDecode(String v) {
        return URLDecoder.decode(v, StandardCharsets.UTF_8);
    }

    private String getCookie(ServerWebExchange exchange, String csrfToken) {
        return Optional.ofNullable(exchange.getRequest().getCookies().getFirst(csrfToken))
                .map(cookie -> URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8))
                .orElse(null);
    }
}
