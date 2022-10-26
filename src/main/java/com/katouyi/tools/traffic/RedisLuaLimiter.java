package com.katouyi.tools.traffic;

import cn.hutool.core.date.DateUtil;
import org.assertj.core.util.Lists;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Optional;

public class RedisLuaLimiter {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public RedisScript<Integer> getRedisScript() {
        DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource( new ResourceScriptSource( new ClassPathResource( "rateLimit.lua" ) ) );
        redisScript.setResultType( Integer.class );
        return redisScript;
    }

    public boolean testLock() {
        Integer execute = redisTemplate.execute( getRedisScript(),
                Lists.newArrayList( "key1" + DateUtil.currentSeconds() ),
                Collections.singletonList( 20 )
        );
        return Optional.ofNullable( execute ).orElse( 0 ) > 0;
    }

}
