package dev.twiceb.common.util;

import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class BaseHostResolver {
    @Value("${web.url:http://localhost:8000}")
    private String webUrl;

    @Value("${admin.base-url:http://localhost:3001}")
    private String adminBaseUrl;

    @Value("${admin.base-path:/god-mode/}")
    private String adminBasePath;

    // @Value("${space.base-url:}")
    // private String spaceBaseUrl;

    // @Value("${space.base-path:/spaces/}")
    // private String spaceBasePath;

    @Value("${app.base-url:http://localhost:3000/}")
    private String appBaseUrl;

    /** primary entry */
    public String resolve(boolean isAdmin, boolean isSpace, boolean isApp) {
        if (isAdmin) {
            String path = normalizePath(adminBasePath);
            return (adminBaseUrl != null && !adminBaseUrl.isBlank()) ? adminBaseUrl + path
                    : webUrl + path;
        }

        if (isApp) {
            return (appBaseUrl != null && !appBaseUrl.isBlank()) ? appBaseUrl : webUrl;
        }

        return webUrl;
    }

    /** for future use; create from scheme/host from request */
    public String resolve(HttpServletRequest request, boolean isAdmin, boolean isSpace,
            boolean isApp) {
        return resolve(isAdmin, isSpace, isApp); // currently ignore request
    }

    public String resolve(ServerHttpRequest request, boolean isAdmin, boolean isSpace,
            boolean isApp) {
        return resolve(isAdmin, isSpace, isApp); // currently ignore requests
    }

    public URI buildUrl(String base, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(base);
        if (params != null) {
            params.forEach((k, v) -> {
                if (v != null) {
                    builder.queryParam(k, v);
                }
            });
        }
        return builder.build(true).toUri();
    }

    public String validateNextPath(String nextPath) {
        if (nextPath == null || nextPath.isBlank())
            return "";

        try {
            URI uri = URI.create(nextPath);
            // reject abs urls, so reject sus
            if (uri.isAbsolute() || uri.getHost() != null) {
                nextPath = uri.getPath();
            }

            if (!nextPath.startsWith("/") || nextPath.contains("..")) {
                return "";
            }

            return nextPath;
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank())
            return "/";
        if (!path.startsWith("/"))
            path = "/" + path;
        if (!path.endsWith("/"))
            path += "/";
        return path;
    }
}
