package dev.twiceb.userservice.service.security;

public interface Hasher {
    String hash(String raw);

    boolean matches(String raw, String hashed);
}
