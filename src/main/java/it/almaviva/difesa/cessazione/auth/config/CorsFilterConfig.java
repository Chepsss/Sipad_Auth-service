package it.almaviva.difesa.cessazione.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Slf4j
@Configuration
public class CorsFilterConfig {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;
    @Value("${app.cors.allowed-methods}")
    private String[] allowedMethods;
    @Value("${app.cors.allowed-headers}")
    private String[] allowedHeaders;
    @Value("${app.cors.max-age}")
    private long maxAge;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        log.debug(">>>>>>>>>>> CORS FILTER CONFIG:::");
        log.debug(String.format("Origins allowed are: %s", String.join(";;", allowedOrigins)));
        log.debug(String.format("Methods allowed are: %s", String.join(";;", allowedMethods)));
        log.debug(String.format("Headers allowed are: %s", String.join(";;", allowedHeaders)));
        log.debug(String.format("Max-age: %s", maxAge));

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));
        config.setAllowedHeaders(Arrays.asList(allowedHeaders));
        config.setAllowedMethods(Arrays.asList(allowedMethods));
        config.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Integer.MIN_VALUE);
        return bean;
    }
}
