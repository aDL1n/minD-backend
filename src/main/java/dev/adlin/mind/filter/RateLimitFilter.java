package dev.adlin.mind.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitFilter implements Filter {
    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final int maxRequests;
    private final long timeWindow;

    public RateLimitFilter(int maxRequests, long timeWindow) {
        this.maxRequests = maxRequests;
        this.timeWindow = timeWindow;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        scheduler.scheduleAtFixedRate(this::cleanupOldCounters, this.timeWindow, this.timeWindow, TimeUnit.SECONDS);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientIp = httpRequest.getRemoteAddr();

        RequestCounter counter = requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());
        if (counter.isExceeded()) {
            ((jakarta.servlet.http.HttpServletResponse) response).sendError(429, "Too Many Requests");
            return;
        }

        counter.increment();
        chain.doFilter(request, response);
    }

    private void cleanupOldCounters() {
        long currentTime = System.currentTimeMillis();
        requestCounts.entrySet().removeIf(entry ->
                currentTime - entry.getValue().getLastResetTime() > this.timeWindow * 2
        );
    }

    @Override
    public void destroy() {
        scheduler.shutdown();
    }

    @Getter
    private class RequestCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private long lastResetTime = System.currentTimeMillis();

        public boolean isExceeded() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastResetTime > timeWindow) {
                count.set(0);
                lastResetTime = currentTime;
            }
            return count.get() >= maxRequests;
        }

        public void increment() {
            count.incrementAndGet();
        }
    }
}
