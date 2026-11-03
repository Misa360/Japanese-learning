package com.jlearn.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * F08: xac dinh loai anh THAT bang noi dung (magic bytes + giai ma), khong tin ten file hay Content-Type client gui.
 * Anh duoc giai ma roi ghi lai (re-encode) nen moi metadata/payload nhung them deu bi loai bo.
 */
public final class ImageSniffer {
    public static final int MAX_DIMENSION = 4000;
    private ImageSniffer() { }

    public static final class Result {
        public final String extension;   // "png" | "jpg"
        public final byte[] cleanBytes;
        Result(String ext, byte[] bytes) { this.extension = ext; this.cleanBytes = bytes; }
    }

    /** @return null neu khong phai JPEG/PNG hop le. */
    public static Result sanitize(byte[] data) {
        String ext = magic(data);
        if (ext == null) return null;
        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) return null;
            ImageReader reader = readers.next();
            try {
                reader.setInput(iis, true, true);
                String fmt = reader.getFormatName().toLowerCase();
                if (!(fmt.equals("png") || fmt.equals("jpeg") || fmt.equals("jpg"))) return null;
                if (fmt.equals("png") != ext.equals("png")) return null;      // magic bytes va dinh dang giai ma phai khop
                int w = reader.getWidth(0), h = reader.getHeight(0);          // doc kich thuoc truoc khi giai ma (chong "bom anh")
                if (w < 1 || h < 1 || w > MAX_DIMENSION || h > MAX_DIMENSION) return null;
                BufferedImage img = reader.read(0);
                if (img == null) return null;
                BufferedImage out = img;
                if (ext.equals("jpg") && img.getColorModel().hasAlpha()) {
                    out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                    out.getGraphics().drawImage(img, 0, 0, java.awt.Color.WHITE, null);
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                if (!ImageIO.write(out, ext.equals("png") ? "png" : "jpg", bos)) return null;
                return new Result(ext, bos.toByteArray());
            } finally {
                reader.dispose();
            }
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    private static String magic(byte[] d) {
        if (d == null || d.length < 12) return null;
        if ((d[0] & 0xFF) == 0x89 && d[1] == 'P' && d[2] == 'N' && d[3] == 'G' && d[4] == 0x0D && d[5] == 0x0A && d[6] == 0x1A && d[7] == 0x0A) return "png";
        if ((d[0] & 0xFF) == 0xFF && (d[1] & 0xFF) == 0xD8 && (d[2] & 0xFF) == 0xFF) return "jpg";
        return null;
    }
}
