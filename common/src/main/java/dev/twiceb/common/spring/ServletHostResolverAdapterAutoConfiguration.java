package dev.twiceb.common.spring;

import java.net.URI;
import java.util.Map;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import dev.twiceb.common.util.HostProps;
import dev.twiceb.common.util.HostResolver;
import jakarta.servlet.http.HttpServletRequest;

@AutoConfiguration
@ConditionalOnClass(HttpServletRequest.class)
public class ServletHostResolverAdapterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    ServletHostResolverAdapter ServletHostResolverAdapter(HostResolver delegate, HostProps props) {
        return new ServletHostResolverAdapter(delegate, props);
    }

    public static final class ServletHostResolverAdapter {
        private final HostResolver delegate;
        @SuppressWarnings("unused")
        private final HostProps props;

        ServletHostResolverAdapter(HostResolver delegate, HostProps props) {
            this.delegate = delegate;
            this.props = props;
        }

        // overload that accepts ServerHttpRequest
        public String resolve(HttpServletRequest request, boolean isAdmin, boolean isSpace,
                boolean isApp, String tenantDomainSlug) {
            return delegate.resolve(isAdmin, isSpace, isApp, tenantDomainSlug);
        }

        public String validateNextPath(String nextPath) {
            return delegate.validateNextPath(nextPath);
        }

        public URI buildUrl(String base, Map<String, String> params) {
            return delegate.buildUrl(base, params);
        }
    }
}
