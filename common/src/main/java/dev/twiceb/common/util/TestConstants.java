package dev.twiceb.common.util;

import java.util.HashSet;
import java.util.Set;

public class TestConstants {
    public static final String USER_EMAIL = "test@test.test";
    public static final String USER_USERNAME = "john.doe123";
    public static final String AUTH_USER_USERNAME = "jane.smith123";
    public static final String AUTH_USER_USERNAME_BAD_LOGIN_ATTEMPTS = "sam.wilson123";
    public static final String AUTH_USER_USERNAME_LOCKED = "lisa.jones123";
    public static final String AUTH_USER_USERNAME_PENDING = "michael.brown123";
    public static final String SAME_USER_EMAIL = "john.doe@example.com";
    public static final String NOT_VALID_EMAIL = "random@random.com";
    public static final String USER_FIRST_NAME = "john";
    public static final String USER_LAST_NAME = "doe";
    public static final String USER_PASSWORD = "password123";
    public static final Long USER_ID = 2L;
    public static final String AUTH_USER_EMAIL = "jane.smith@example.com";
    public static final String AUTH_USER_FIRST_NAME = "Jane";
    public static final String AUTH_USER_LAST_NAME = "Smith";
    public static final Long NEW_DEVICE_USER_ID = 5L;
    public static final String NEW_DEVICE_USER_EMAIL = "michael.brown@example.com";
    public static final String NEW_DEVICE_FIRST_NAME = "Michael";
    public static final String NEW_DEVICE_LAST_NAME = "Brown";

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; I; Intel Mac OS X 11_7_9; de-LI; rv:1.9b4) Gecko/2012010317 Firefox/10.0a4";
    public static final String USER_IP = "216.111.82.173";

    // password-service
    public static final Long ENCRYPTION_ID = 1L;
    public static final String DOMAIN = "jesus.com";
    public static final String SAME_DOMAIN = "google.com";
    public static final String WEBSITE_URL = "www.google.com";
    public static final String SAME_WEBSITE_URL = "www.jesus.com";
    public static final String DOMAIN_USERNAME = "random123";
    public static final String DOMAIN_PASSWORD = "Twice_Mina1";
    public static final String DOMAIN_CONFIRM_PASSWORD = "Twice_Mina1";
    public static final String DOMAIN_NOTES = "random note here";
    public static final Long DOMAIN_EXPIRY_POLICY = 1L;
    public static final Set<Long> DOMAIN_CATEGORIES = Set.of(1L, 2L);
    public static final Set<Long> INVALID_DOMAIN_CATEGORIES = Set.of(-1L, 0L);
}
