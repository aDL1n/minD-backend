package dev.adlin.mind.config;

import dev.adlin.mind.filter.RateLimitFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("rate-limiter")
public class FilterConfig {

    private int maxRequests = 50;
    private long timeWindow = 60;

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter() {
        FilterRegistrationBean<RateLimitFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new RateLimitFilter(this.maxRequests, this.timeWindow));
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    public long getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(long timeWindow) {
        this.timeWindow = timeWindow;
    }
}
