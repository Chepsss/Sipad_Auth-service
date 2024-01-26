package it.almaviva.difesa.cessazione.auth.config;

import it.almaviva.difesa.cessazione.auth.interceptor.AuthorizationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    public static final List<String> EXCLUDED_SPECIFIC_PATHS = List.of(
            "/auth/authorize",
            "/auth/refreshToken",
            "/v2/api-docs",
            "/swagger-ui.html"
    );

    public static final List<String> EXCLUDED_ABSOLUTE_PATHS = List.of(
            "/swagger-resources/**",
            "/webjars/**",
            "/h2-console/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/info"
    );

    protected static final List<String> EXCLUDED_PATHS = Stream.concat(EXCLUDED_SPECIFIC_PATHS.stream(), EXCLUDED_ABSOLUTE_PATHS.stream()).collect(Collectors.toList());

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDED_PATHS);
    }

    @Bean
    public AuthorizationInterceptor authorizationInterceptor(){
        return new AuthorizationInterceptor();
    }
}
