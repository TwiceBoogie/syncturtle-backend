package dev.twiceb.passwordservice.model.support;

// jpa converters are constructed by hibernate.
// static holder that spring setups
public final class CryptoHolder {
    private static volatile CryptoPort PORT;

    private CryptoHolder() {
    }

    public static void set(CryptoPort p) {
        PORT = p;
    }

    public static CryptoPort get() {
        if (PORT == null) {
            throw new IllegalStateException("cryptoPort not initialized");
        }
        return PORT;
    }
}
