package com.katouyi.tools.jedis;


import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public enum RedisKey {

    TEST_KEY("test_key", 1)
    ;

    private final String redisKeyPrefix;

    private final int expire;

    RedisKey(String redisKeyPrefix, int expire) {
        this.redisKeyPrefix = redisKeyPrefix;
        this.expire = expire;
    }

    public String getRedisKeyPrefix() {
        return redisKeyPrefix;
    }

    public int getExpire() {
        return expire;
    }

    public String makeRedisKey(Object... details) {
        if (details == null || details.length == 0) {
            return getRedisKeyPrefix();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(getRedisKeyPrefix());
        for (int i = 0; i < details.length; i++) {
            if (Objects.isNull(details[i]) || StringUtils.isBlank(details[i].toString())) {
                continue;
            }
            builder.append(details[i]);
            if (i + 1 != details.length) {
                builder.append("#");
            }
        }
        return builder.toString();
    }
}
