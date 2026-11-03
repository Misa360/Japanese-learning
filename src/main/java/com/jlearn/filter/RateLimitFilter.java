package com.jlearn.filter;

import com.jlearn.config.AppConfig;
import com.jlearn.util.ClientIp;
import com.jlearn.util.WebUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * F01 - RateLimitMiddleware: gioi han so POST /register (va /login) theo IP, cua so truot, trong bo nho cua tung node.
 * Chay nhieu node sau load balancer thi gioi han theo tung node; muon chung toan he thong thi chuyen sang Redis/nginx limit_req.
 */
public class RateLimitFilter implements Filter {
    private static final class Rule {
        final int max; final long windowMs;
        Rule(int max, long windowMs) { this.max = max; this.windowMs = windowMs; }
    }

    private final ConcurrentHashMap<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    private Rule ruleFor(String method, String path) {
        if (!"POST".equalsIgnoreCase(method)) return null;
        if ("/register".equals(path)) {
            return new Rule(AppConfig.getInt("ratelimit.register.max", 5), AppConfig.getInt("ratelimit.register.windowSeconds", 3600) * 1000L);
        }
        if ("/login".equals(path)) {
            return new Rule(AppConfig.getInt("ratelimit.login.max", 60), AppConfig.getInt("ratelimit.login.windowSeconds", 600) * 1000L);
        }
        return null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = WebUtil.path(req);
        Rule rule = ruleFor(req.getMethod(), path);
        if (rule == null) { chain.doFilter(request, response); return; }

        long now = System.currentTimeMillis();
        String key = path + "|" + ClientIp.of(req);
        if (hits.size() > 20000) purge(now);
        Deque<Long> dq = hits.computeIfAbsent(key, k -> new ArrayDeque<Long>());
        boolean allowed;
        synchronized (dq) {
            while (!dq.isEmpty() && now - dq.peekFirst() > rule.windowMs) dq.pollFirst();
            allowed = dq.size() < rule.max;
            if (allowed) dq.addLast(now);
        }
        if (!allowed) {
            resp.setHeader("Retry-After", String.valueOf(rule.windowMs / 1000));
            WebUtil.reject(req, resp, 429, "Bạn thao tác quá nhiều lần. Vui lòng thử lại sau.");
            return;
        }
        chain.doFilter(request, response);
    }

    private void purge(long now) {
        hits.entrySet().removeIf(e -> { synchronized (e.getValue()) {
            Long last = e.getValue().peekLast();
            return last == null || now - last > 3600_000L; } });
    }
}
