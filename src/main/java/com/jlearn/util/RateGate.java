package com.jlearn.util;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/** Gioi han tan suat theo (session, khoa): cho phep toi da 1 lan moi minMillis. Dung cho F16. */
public final class RateGate {
    private static final String ATTR = "rateGate";
    private RateGate() { }

    @SuppressWarnings("unchecked")
    public static boolean allow(HttpSession session, String key, long minMillis) {
        synchronized (session) {
            Map<String, Long> m = (Map<String, Long>) session.getAttribute(ATTR);
            if (m == null) { m = new HashMap<>(); session.setAttribute(ATTR, m); }
            long now = System.currentTimeMillis();
            Long last = m.get(key);
            if (last != null && now - last < minMillis) return false;
            if (m.size() > 200) m.clear();   // khong de map phinh to
            m.put(key, now);
            return true;
        }
    }
}
