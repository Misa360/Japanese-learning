package com.jlearn.util;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * F14: chi cho phep nhung video tu domain trong danh sach cho phep.
 *  - YouTube: chi lay video id (11 ky tu), server tu dung URL nhung (youtube-nocookie), khong dung URL tho cua admin.
 *  - File video truc tiep (.mp4/.webm): chi khi host nam trong DIRECT_VIDEO_HOSTS (mac dinh rong).
 * Muon them nguon: sua 2 tap hop duoi day, dong thoi them vao CSP (frame-src / media-src).
 */
public final class VideoEmbed {
    private static final Set<String> YOUTUBE_HOSTS = new HashSet<>(Arrays.asList(
            "youtube.com", "www.youtube.com", "m.youtube.com", "youtu.be",
            "youtube-nocookie.com", "www.youtube-nocookie.com"));
    private static final Set<String> DIRECT_VIDEO_HOSTS = new HashSet<>();   // vi du: "cdn.jlearn.vn"
    private static final Pattern YT_ID = Pattern.compile("^[A-Za-z0-9_-]{11}$");
    private static final Pattern YT_PATH = Pattern.compile("^/(?:embed|shorts|live|v)/([A-Za-z0-9_-]{11})(?:/.*)?$");

    private VideoEmbed() { }

    private static URI parseHttps(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty() || s.length() > 500) return null;
        try {
            URI u = new URI(s);
            if (!"https".equalsIgnoreCase(u.getScheme()) || u.getHost() == null || u.getUserInfo() != null) return null;
            return u;
        } catch (Exception e) {
            return null;
        }
    }

    /** @return id video YouTube hop le, hoac null. */
    public static String youtubeId(String raw) {
        URI u = parseHttps(raw);
        if (u == null) return null;
        String host = u.getHost().toLowerCase(Locale.ROOT);
        if (!YOUTUBE_HOSTS.contains(host)) return null;
        String path = u.getPath() == null ? "" : u.getPath();
        if (host.equals("youtu.be")) {
            String id = path.startsWith("/") ? path.substring(1) : path;
            return YT_ID.matcher(id).matches() ? id : null;
        }
        if (path.equals("/watch")) {
            String q = u.getRawQuery();
            if (q == null) return null;
            for (String kv : q.split("&")) {
                if (kv.startsWith("v=")) {
                    String id = kv.substring(2);
                    return YT_ID.matcher(id).matches() ? id : null;
                }
            }
            return null;
        }
        java.util.regex.Matcher m = YT_PATH.matcher(path);
        return m.matches() ? m.group(1) : null;
    }

    public static String youtubeEmbedUrl(String id) {
        return "https://www.youtube-nocookie.com/embed/" + id;
    }

    /** @return URL file video truc tiep neu host duoc phep, hoac null. */
    public static String directVideoUrl(String raw) {
        URI u = parseHttps(raw);
        if (u == null) return null;
        if (!DIRECT_VIDEO_HOSTS.contains(u.getHost().toLowerCase(Locale.ROOT))) return null;
        String p = u.getPath() == null ? "" : u.getPath().toLowerCase(Locale.ROOT);
        return (p.endsWith(".mp4") || p.endsWith(".webm")) ? raw.trim() : null;
    }

    /** Link tai lieu: chi http/https, de khong bi nhung javascript: hay data:. */
    public static String safeHttpUrl(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty() || s.length() > 1000) return null;
        try {
            URI u = new URI(s);
            String sch = u.getScheme();
            if (sch == null || u.getHost() == null) return null;
            return ("https".equalsIgnoreCase(sch) || "http".equalsIgnoreCase(sch)) ? s : null;
        } catch (Exception e) {
            return null;
        }
    }
}
