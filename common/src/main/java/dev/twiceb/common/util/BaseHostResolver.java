package dev.twiceb.common.util;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

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

    public String resolve(ServerHttpRequest request, boolean isAdmin, boolean isSpace,
            boolean isApp) {
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

    public String validateNextPath(String nextPath) {
        if (nextPath == null || nextPath.isBlank())
            return "";

        try {
            URI uri = new URI(nextPath);

            // reject abs urls, so reject sus
            if (uri.isAbsolute() || uri.getHost() != null) {
                nextPath = uri.getPath();
            }

            if (!nextPath.startsWith("/")) {
                return "";
            }

            // reject path traversal attempts
            if (nextPath.contains("..")) {
                return "";
            }

            return nextPath;
        } catch (URISyntaxException e) {
            return "";
        }
    }

    private String normalizePath(String path) {
        if (!path.startsWith("/"))
            path = "/" + path;
        if (!path.endsWith("/"))
            path += "/";
        return path;
    }
}
