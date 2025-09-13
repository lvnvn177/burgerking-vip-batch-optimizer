package com.burgerking.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 관련 설정을 위한 클래스입니다. (CORS 설정 등)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 전역 CORS(Cross-Origin Resource Sharing) 설정을 추가합니다.
     * - 특정 Origin 주소 지정 
     * - 허용할 HTTP 메서드와 헤더를 지정합니다.
     *
     * @param registry CORS 설정을 등록하는 데 사용됩니다.
     */
    @SuppressWarnings("null")
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:3000", "http://frontend.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
