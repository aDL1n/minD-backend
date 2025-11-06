package dev.adlin.mind.config;

import dev.adlin.mind.filter.RateLimitFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("rate-limiter")
@Getter
@Setter
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
}
