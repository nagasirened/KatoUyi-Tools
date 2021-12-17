package com.katouyi.tools.redis.jedis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

@EnableConfigurationProperties(RedisProperties.class)
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisProperties {
  private boolean cluster;
  private List<String> hosts;
  private Integer port;
  private String password;
}
