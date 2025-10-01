package dev.twiceb.common.spring;

import java.net.URI;
import java.util.Map;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import dev.twiceb.common.util.HostProps;
import dev.twiceb.common.util.HostResolver;

@AutoConfiguration
@ConditionalOnClass(ServerHttpRequest.class)
public class WebfluxHostResolverAdapterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    WebfluxHostResolverAdapter WebfluxHostResolverAdapter(HostResolver delegate, HostProps props) {
        return new WebfluxHostResolverAdapter(delegate, props);
    }

    public static final class WebfluxHostResolverAdapter {
        private final HostResolver delegate;
        private final HostProps props;

        WebfluxHostResolverAdapter(HostResolver delegate, HostProps props) {
            this.delegate = delegate;
            this.props = props;
        }

        // overload that accepts ServerHttpRequest
        public String resolve(ServerHttpRequest request, boolean isAdmin, boolean isSpace,
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
