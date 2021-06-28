package com.katouyi.tools.jedis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

/**
 * 包名:com.quick.music.data.config<br>
 *
 * <p>功能:
 *
 * @date:2019/9/19 <br>
 * @version:1.0 <br>
 */
@EnableConfigurationProperties(RedisProperties.class)
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisProperties {
  private boolean cluster;
  private List<String> hosts;
  private Integer port;
  private String password;
}
