package dev.twiceb.common.util;

import java.net.URI;
import java.util.Map;

public interface HostResolver {
    String resolve(boolean isAdmin, boolean isSpace, boolean isApp, String tenantDomainSlug);

    URI buildUrl(String base, Map<String, String> params);

    String validateNextPath(String nextPath);
}
