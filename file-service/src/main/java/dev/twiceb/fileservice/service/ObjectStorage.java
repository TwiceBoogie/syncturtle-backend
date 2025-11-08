package dev.twiceb.fileservice.service;

import java.io.File;

public interface ObjectStorage {
    String put(String bucket, String key, File file);

    byte[] get(String bucket, String key);

    void delete(String bucket, String key);
}
