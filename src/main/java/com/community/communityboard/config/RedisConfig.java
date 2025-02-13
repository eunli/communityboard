package com.community.communityboard.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

  private final Environment env;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    String host = env.getProperty("spring.redis.host", "localhost");
    int port = Integer.parseInt(env.getProperty("spring.redis.port", "6379"));
    return new LettuceConnectionFactory(host, port);
  }

  @Bean
  public StringRedisTemplate redisTemplate() {
    StringRedisTemplate template = new StringRedisTemplate();
    template.setConnectionFactory(redisConnectionFactory());
    return template;
  }
}
