package com.burgerking.common.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ConfigurationProperties(prefix = "server")
@PropertySources({
        @PropertySource("classpath:properties/env.properties") // env.properties 파일 소스 등록
})
public class PropertyConfig {

}