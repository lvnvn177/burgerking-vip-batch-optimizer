package com.burgerking.coupon.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 쿠폰 모듈에서 사용하는 Redis 설정
 * 분산 락과 쿠폰 발급 관련 데이터를 위한 Redis 연결 설정을 정의합니다.
 */
@Configuration
public class CouponRedisConfig {
    
    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    /**
     * Redis 연결 팩토리를 생성합니다.
     */
    @Bean
    public RedisConnectionFactory couponRedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);

        if (!redisPassword.isEmpty()) {
            redisConfig.setPassword(redisPassword);
        }

        return new LettuceConnectionFactory(redisConfig);
    }

    /**
     * 쿠폰 모듈에서 사용할 RedisTemplate을 설정합니다.
     * 주로 문자열 키와 값을 다루므로 StringRedisSerializer를 사용합니다.
     */
    @Bean
    public RedisTemplate<String, String> coupRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(couponRedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
