package com.jlearn.util;

import com.jlearn.config.AppConfig;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;

/** BCrypt. Hash seed PHP ($2y$) tuong thich: doi tien to thanh $2a$ truoc khi kiem tra. */
public final class PasswordHasher {
    private static volatile String dummyHash;

    private PasswordHasher() { }

    private static int cost() {
        int c = AppConfig.getInt("security.bcrypt.cost", 12);
        return Math.max(4, Math.min(c, 16));
    }

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(cost()));
    }

    /** Luon ton thoi gian BCrypt du user co ton tai hay khong (giam kha nang do tai khoan qua thoi gian phan hoi). */
    public static boolean verify(String plain, String hashOrNull) {
        String h = hashOrNull;
        if (h == null || h.length() < 20) h = dummy();
        if (h.startsWith("$2y$") || h.startsWith("$2b$")) h = "$2a$" + h.substring(4);
        try {
            boolean ok = BCrypt.checkpw(plain, h);
            return ok && hashOrNull != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static String dummy() {
        if (dummyHash == null) {
            synchronized (PasswordHasher.class) {
                if (dummyHash == null) dummyHash = BCrypt.hashpw("jlearn-dummy-" + System.nanoTime(), BCrypt.gensalt(cost()));
            }
        }
        return dummyHash;
    }

    /** BCrypt chi dung 72 byte dau; tu choi dai hon thay vi cat im lang. */
    public static boolean tooLong(String plain) {
        return plain.getBytes(StandardCharsets.UTF_8).length > 72;
    }
}
