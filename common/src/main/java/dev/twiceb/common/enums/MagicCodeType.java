package dev.twiceb.common.enums;

public enum MagicCodeType {
    MAGIC_LINK("magic"), DEVICE_VERIFICATION("device");

    private final String prefix;

    MagicCodeType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String buildRedisKey(String email) {
        return prefix + "_" + email;
    }
}
