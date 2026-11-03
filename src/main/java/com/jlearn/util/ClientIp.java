package com.jlearn.util;

import com.jlearn.config.AppConfig;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

public final class ClientIp {
    private ClientIp() { }

    /** IP client. Chi tin X-Forwarded-For khi security.trustProxy=true (dang chay sau nginx/LB tin cay), vi header nay gia mao duoc. */
    public static String of(HttpServletRequest req) {
        if (AppConfig.getBool("security.trustProxy", false)) {
            String xff = req.getHeader("X-Forwarded-For");
            if (xff != null && !xff.trim().isEmpty()) {
                String first = xff.split(",")[0].trim();
                if (first.matches("^[0-9a-fA-F:.]{3,45}$")) return first;
            }
        }
        return req.getRemoteAddr() == null ? "0.0.0.0" : req.getRemoteAddr();
    }

    /** Dang nhi phan cho cot VARBINARY(16) (IPv4: 4 byte, IPv6: 16 byte). null neu khong doc duoc. */
    public static byte[] bytes(String ip) {
        try {
            if (ip == null || !ip.matches("^[0-9a-fA-F:.]{3,45}$")) return null;   // tranh tra cuu DNS
            return InetAddress.getByName(ip).getAddress();
        } catch (Exception e) {
            return null;
        }
    }
}
