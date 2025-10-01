package dev.twiceb.common.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import dev.twiceb.common.util.HostProps;
import dev.twiceb.common.util.HostResolver;

@AutoConfiguration
@EnableConfigurationProperties(HostProps.class)
public class HostResolverAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    HostResolver hostResolver(HostProps props) {
        return new PropsBackedHostResolver(props);
    }
}
