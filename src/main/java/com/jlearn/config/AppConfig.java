package com.jlearn.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/** Doc app.properties (classpath), cho phep ghi de bang bien moi truong / -D. Thay cho config/*.php. */
public final class AppConfig {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = AppConfig.class.getResourceAsStream("/app.properties")) {
            if (in != null) PROPS.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private AppConfig() { }

    public static String get(String key, String def) {
        String envKey = "JLEARN_" + key.toUpperCase().replace('.', '_');
        String v = System.getenv(envKey);
        if (v == null || v.isEmpty()) v = System.getProperty(envKey);
        if (v == null || v.isEmpty()) v = PROPS.getProperty(key);
        return (v == null || v.trim().isEmpty()) ? def : v.trim();
    }

    public static int getInt(String key, int def) {
        try { return Integer.parseInt(get(key, String.valueOf(def))); } catch (NumberFormatException e) { return def; }
    }

    public static boolean getBool(String key, boolean def) {
        return Boolean.parseBoolean(get(key, String.valueOf(def)));
    }

    /** Thu muc anh dai dien, nam NGOAI webapp de khong ai goi truc tiep duoc va khong bi thuc thi nhu ma. */
    public static Path uploadDir() {
        String cfg = get("upload.dir", "");
        if (!cfg.isEmpty()) return Paths.get(cfg).toAbsolutePath().normalize();
        String base = System.getProperty("catalina.base");
        if (base != null && !base.isEmpty()) return Paths.get(base, "jlearn-storage", "uploads").toAbsolutePath().normalize();
        return Paths.get("storage", "uploads").toAbsolutePath().normalize();
    }
}
