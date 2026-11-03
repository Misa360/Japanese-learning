package com.jlearn.service;

import com.jlearn.config.AppConfig;
import com.jlearn.exception.ValidationException;
import com.jlearn.util.ImageSniffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.regex.Pattern;

/** F08 - luu anh dai dien: kiem tra noi dung that, ghi lai anh, ten ngau nhien, luu NGOAI webapp. */
public class AvatarService {
    public static final long MAX_BYTES = 2L * 1024 * 1024;
    public static final Pattern STORED_NAME = Pattern.compile("^[0-9a-f]{32}\\.(png|jpg)$");
    private static final SecureRandom RNG = new SecureRandom();

    /** @return ten file da luu (vd "3fa9...c1.png"). Nem ValidationException voi thong bao than thien neu khong hop le. */
    public String store(InputStream in) throws IOException {
        byte[] data = readLimited(in);
        ImageSniffer.Result img = ImageSniffer.sanitize(data);
        if (img == null) throw new ValidationException("Ảnh đại diện phải là file JPG hoặc PNG hợp lệ (tối đa 4000×4000 px)");

        byte[] rnd = new byte[16];
        RNG.nextBytes(rnd);
        StringBuilder sb = new StringBuilder();
        for (byte b : rnd) sb.append(String.format("%02x", b));
        String name = sb.append('.').append(img.extension).toString();

        Path dir = AppConfig.uploadDir();
        Files.createDirectories(dir);
        Path tmp = Files.createTempFile(dir, "up-", ".tmp");
        try {
            Files.write(tmp, img.cleanBytes);
            Files.move(tmp, dir.resolve(name), StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(tmp);
        }
        return name;
    }

    /** Xoa anh cu (chi khi ten dung dinh dang do minh tao: chong xoa file ngoai y muon). */
    public void deleteQuietly(String storedName) {
        if (storedName == null || !STORED_NAME.matcher(storedName).matches()) return;
        try { Files.deleteIfExists(AppConfig.uploadDir().resolve(storedName)); } catch (IOException ignore) { /* khong anh huong nguoi dung */ }
    }

    public Path resolve(String storedName) {
        if (storedName == null || !STORED_NAME.matcher(storedName).matches()) return null;
        return AppConfig.uploadDir().resolve(storedName);
    }

    private byte[] readLimited(InputStream in) throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        long total = 0;
        int n;
        while ((n = in.read(buf)) > 0) {
            total += n;
            if (total > MAX_BYTES) throw new ValidationException("Ảnh quá lớn (tối đa 2 MB)");
            out.write(buf, 0, n);
        }
        return out.toByteArray();
    }
}
