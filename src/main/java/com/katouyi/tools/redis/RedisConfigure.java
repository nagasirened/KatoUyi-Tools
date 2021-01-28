package com.katouyi.tools.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * <p>
 *
 * @description: redis配置相关
 * </p>
 * @author: ZengGuangfu
 * @since 2019-09-2019/9/20
 */

@Configuration
public class RedisConfigure {

    /**
     * https://www.cnblogs.com/superfj/p/9232482.html
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        /*//使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jacksonSeial = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);*/

        /**
         * 不使用上面的jacksonSeial 反序列化的时候出了问题
             *  2019-09-23 13:54:57.993 ERROR 11900 --- [nio-8080-exec-7] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [org.springframework.data.redis.serializer.SerializationException: Could not read JSON: Unrecognized field "empty" (class org.apache.shiro.subject.SimplePrincipalCollection), not marked as ignorable (one known property: "realmNames"])
             *  at [Source: (byte[])"{"@class":"org.apache.shiro.authc.SimpleAuthenticationInfo","principals":{"@class":"org.apache.shiro.subject.SimplePrincipalCollection","empty":false,"primaryPrincipal":"zengg","realmNames":["java.util.LinkedHashMap$LinkedKeySet",["HegemonyRealmName"]]},"credentials":"zengg","credentialsSalt":null}"; line: 1, column: 150] (through reference chain: org.apache.shiro.authc.SimpleAuthenticationInfo["principals"]->org.apache.shiro.subject.SimplePrincipalCollection["empty"]); nested exception is com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field "empty" (class org.apache.shiro.subject.SimplePrincipalCollection), not marked as ignorable (one known property: "realmNames"])
             *  at [Source: (byte[])"{"@class":"org.apache.shiro.authc.SimpleAuthenticationInfo","principals":{"@class":"org.apache.shiro.subject.SimplePrincipalCollection","empty":false,"primaryPrincipal":"zengg","realmNames":["java.util.LinkedHashMap$LinkedKeySet",["HegemonyRealmName"]]},"credentials":"zengg","credentialsSalt":null}"; line: 1, column: 150] (through reference chain: org.apache.shiro.authc.SimpleAuthenticationInfo["principals"]->org.apache.shiro.subject.SimplePrincipalCollection["empty"])] with root cause
         */
        // 值采用json序列化

        template.setValueSerializer(new JdkSerializationRedisSerializer());
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        template.afterPropertiesSet();

        return template;
    }

    @Bean(name = "longRedisTemplate")
    public RedisTemplate<String, Long> longRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new GenericToStringSerializer<Long>(Long.class));
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 对hash类型的数据操作
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 对redis字符串类型数据操作
     * @param redisTemplate
     * @return
     */
    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 对链表类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 对无序集合类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 对有序集合类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

}
