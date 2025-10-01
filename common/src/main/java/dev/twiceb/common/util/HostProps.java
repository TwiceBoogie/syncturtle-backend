package dev.twiceb.common.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class HostProps {
    @SuppressWarnings("unused")
    private String rootDomain = "localhost";
    @SuppressWarnings("unused")
    private String webUrl = "http://localhost";
    @SuppressWarnings("unused")
    private String adminBaseUrl = "http://localhost";
    @SuppressWarnings("unused")
    private String adminBasePath = "/god-mode";
    @SuppressWarnings("unused")
    private String appBaseUrl = "http://localhost";
}
