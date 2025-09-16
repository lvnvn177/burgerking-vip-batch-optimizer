package com.burgerking.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {


    @Value("${SERVER_HOST}")
    private String serverHost;

    @Value("${SERVER_PORT}")
    private String serverPort;

    @Value("${LOCAL_HOST}")
    private String localHost;

   @Value("${LOCAL_PORT}")
    private String localPort;

    @Bean
    public OpenAPI openAPI() {
       // API 기본 정보 설정
       Info info = new Info()
          .title("BurgerKing Backend System API Document")
          .version("1.0")
          .description("BurgerKing 백엔드 시스템 API 문서입니다.")
          .contact(new io.swagger.v3.oas.models.info.Contact().email("billage.official@gmail.com")); // Placeholder email

       // JWT 인증 방식 설정
       String jwtScheme = "jwtAuth";
       SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtScheme);
       Components components = new Components()
          .addSecuritySchemes(jwtScheme, new SecurityScheme()
             .name("Authorization")
             .type(SecurityScheme.Type.HTTP)
             .in(SecurityScheme.In.HEADER)
             .scheme("Bearer")
             .bearerFormat("JWT"));

       // Swagger UI 설정 및 보안 추가
       return new OpenAPI()
          .addServersItem(new Server().url("http://" + localHost + ":" + localPort))  // 추가적인 서버 URL 설정 가능
          .components(components)
          .info(info)
          .addSecurityItem(securityRequirement);
    }

    @Bean
    public GroupedOpenApi membershipApi() {
        return GroupedOpenApi.builder()
                .group("Membership API")
                .pathsToMatch("/api/membership/**")
                .build();
    }
}