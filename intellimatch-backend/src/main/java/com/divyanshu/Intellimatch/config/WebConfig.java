package com.divyanshu.Intellimatch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        String frontendUrl = "http://localhost:3000"; // Default value
        System.out.println("Frontend URL: " + frontendUrl);
        registry.addMapping("/api/**")
            .allowedOrigins(frontendUrl != null ? frontendUrl : "")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
