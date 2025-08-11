package dev.twiceb.userservice.service;

import java.util.Map;

public interface FeatureFlagService {

    Map<String, String> getConfig();

    public String get(String key);
}
