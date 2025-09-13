package com.burgerking.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration // Spring Container Bean 등록을 위해 설정 
public class RedisConfig {
    
    @Value("${spring.redis.host:localhost}") // @Value를 통해 redis 설정 값 주입 
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() { // single node에 redis 연결 
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        if (!redisPassword.isEmpty()) {
            redisConfig.setPassword(redisPassword);
        }
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}

// 설명 

// 설정값(주소, 포트, 패스워드)을 담은 RedisStandaloneConfiguration 객체를 생성 후
// RedisTemplate를 통해 데이터를 주고 받을때는 Key 값을 String 타입으로 Value를 Json 형식으로
// 변환하여 처리하도록 하였습니다.

// @Bean 어노테이션으로 Redis 서버 설정 정보를 담은 객체 및 해당 객체를 사용하는 RedisTemplate 을
// 등록하여 Spring 애플리케이션 실행 시 Redis 단일 서버가 실행됩니다. 


// 참고 

// 설정값(주소, 포트, 패스워드)을 담은 RedisStandaloneConfiguration 객체를 생성 후
// RedisTemplate를 통해 데이터를 주고 받을때는 Key 값을 String 타입으로 Value를 Json 형식으로
// 변환하여 처리하도록 하였습니다.

// @Bean 어노테이션으로 Redis 서버 설정 정보를 담은 객체 및 해당 객체를 사용하는 RedisTemplate 을
// 등록하여 Spring 애플리케이션 실행 시 Redis 단일 서버가 실행됩니다. 


// redisConnectionFactory

// RedisConnectionFactory 
// Redis 서버와의 연결(Connection)을 생성하는 방법을 정의한 Factory Interface 
// Spring Data Redis는 해당 Factory를 통해 얻은 Connection으로 Redis와 통신

// RedisStandaloneConfiguration
// Redis의 여러 방식(Standalone, Sentinel, Cluster) 중 단일 서버(Standalone)에 연결하기 위한 
// 설정 정보를 담은 객체

// 만약 여러 Redis 서버로 구성된 Cluster에 연결한다면 RedisClusterConfiguration 같은 다른 설정 객체를 
// 사용해야 함 

// LettuceConnectionFactory
// Spring Boot 2.x부터 기본 Redis 클라이언트로 사용되는 Lettuce를 이용해 실제 Redis의 연결을 관리
// Lettuce는 Netty 기반의 비동기 이벤트 기반 통신을 지원하여 성능이 뛰어남 


// RedisTemplate
// Redis의 다양한 데이터 타입<String, List, Set, Hash)에 대한 커맨드를 편리하게 실행할 수 있도록 
// 템플릿 메서드 패턴을 적용한 클래스

// 제네릭 타입 지정 <Key, Value>로 지정할 시 ex) <String, ObjecT> 이면 키 값은 문자열 타입이고
// Value는 모든 타입이 올 수 있다는 것을 의미





// redisConfig 
// 해당 설정 값은 @Value를 통해 redis 설정 값을 하드코딩 하지 않고 주입

// @Bean 
// Spring loC 컨테이너가 관리하는 Bean 객체 생성 
// Spring 애플리케이션이 실행될 때 해당 어노테이션이 붙은 함수가 실행 

// 해당 함수에서 반환된 객체 (여기서는 LettuceConnectionFactory 타입) 는 다른 컴포넌트에서 의존성 주입(DI)를 통해 자유롭게 사용 가능 