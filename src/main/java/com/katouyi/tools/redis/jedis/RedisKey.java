package com.katouyi.tools.redis.jedis;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

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

    /**
     * 拼接RedisKey
     */
    public String makeRedisKey(String... details) {
        return makeRedisKeyWithSeparator(StrUtil.COLON, details);
    }

    /**
     * 拼接RedisKey,自定义分隔符号
     */
    public String makeRedisKeyWithSeparator(String separator, String... args) {
        if (ArrayUtil.isEmpty(args)) {
            return getRedisKeyPrefix();
        }
        StringBuilder builder = new StringBuilder(getRedisKeyPrefix());
        int length = args.length;
        for (int i = 0; i < length; i++) {
            if (StrUtil.isBlank(args[i])) {
                continue;
            }
            builder.append(args[i]);
            if (i + 1 != length) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }
}
