package dev.twiceb.common.spring;

import java.net.URI;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;
import dev.twiceb.common.util.HostProps;
import dev.twiceb.common.util.HostResolver;

final class PropsBackedHostResolver implements HostResolver {

    private static final String PROTO = "http://";
    private final HostProps p;

    PropsBackedHostResolver(HostProps props) {
        this.p = props;
    }

    @Override
    public String resolve(boolean isAdmin, boolean isSpace, boolean isApp,
            String tenantDomainSlug) {
        if (isAdmin) {
            String path = normalizePath(p.getAdminBasePath());
            return (p.getAdminBasePath() != null && !p.getAdminBasePath().isBlank())
                    ? p.getAdminBaseUrl() + path
                    : p.getWebUrl() + path;
        }

        if (isApp) {
            if (!tenantDomainSlug.isBlank()) {
                String subdomain = normalizePath(tenantDomainSlug);
                return PROTO + p.getRootDomain() + subdomain;
            }
            return (p.getAppBaseUrl() != null && !p.getAppBaseUrl().isBlank()) ? p.getAppBaseUrl()
                    : p.getWebUrl();
        }

        return p.getWebUrl();
    }

    @Override
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

    @Override
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
