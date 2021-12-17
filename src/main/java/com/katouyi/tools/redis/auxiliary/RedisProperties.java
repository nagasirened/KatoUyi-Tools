package com.katouyi.tools.redis.auxiliary;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * @description: Redis配置信息
 * </p>
 * @author: ZengGuangfu
 */

@Data
@Component("redisProperties1")
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    private String host;

    private int port;

    private int database;

    private String password;
}
