package com.nagasirened.common.redis;

import com.nagasirened.common.redis.prefix.Prefix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @description: redis操作工具
 * </p>
 * @author: ZengGuangfu
 * @since 2019-09-2019/9/20
 */

@Slf4j
@Component
public class RedisAuxiliary {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ValueOperations<String, Object> valueOperations;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "longRedisTemplate")
    private RedisTemplate<String, Long> longRedisTemplate;

    /**
     * LUA脚本释放
     */
    final String LUA_DELETE_LOCK = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

    /**
     * 存储，没有时间限制
     */
    public void stringSetWithoutExpire(Prefix prefix, String key , String value){
        stringRedisTemplate.opsForValue().set(getCurrentKey(prefix, key), value);
    }

    public void stringSetWithExpire(Prefix prefix, String key ,Object value){
        valueOperations.set( getCurrentKey(prefix, key), value, prefix.expire(), TimeUnit.SECONDS);
    }

    public Object stringGet(Prefix prefix, String key){
        return stringRedisTemplate.opsForValue().get( getCurrentKey(prefix,key));
    }

    /**
     * 存储，没有时间限制
     */
    public void setWithoutExpire(Prefix prefix, String key , Object value){
        valueOperations.set( getCurrentKey(prefix, key), value, 0L);
    }

    public void setWithoutExpire(String key, Object value){
        valueOperations.set( key, value, 0L);
    }

    /**
     * 存储，按照设定的时间限制，单位为秒
     */
    public void setWithExpire(Prefix prefix, String key ,Object value){
        valueOperations.set( getCurrentKey(prefix, key), value, prefix.expire(), TimeUnit.SECONDS);
    }

    public void setWithExpire(String key ,Object value, long expireTime){
        valueOperations.set( key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取对象
     */
    public Object get(Prefix prefix, String key){
        return valueOperations.get( getCurrentKey(prefix,key));
    }

    public Object get(String key){
        Object object = valueOperations.get(key);
        if (Objects.isNull(object) || object.toString().equals("empty")){
            log.debug("从缓存中获取的对象是null,该key可能不存在");
            return null;
        }
        return object;
    }

    /**
     * @Limit 登录限制中，使用该方法获取数字
     */
    public Long getLong(String key){
        try{
            Long result = (Long)longRedisTemplate.opsForValue().get(key);
            return result;
        }catch (Exception e){
            log.error("从缓存中获取Long 出错，请检查数据类型");
            return 0L;
        }
    }

    /**
     * 删除对象
     */
    public void delete(Prefix prefix, String key){
        redisTemplate.delete(getCurrentKey(prefix, key));
    }

    public void delete(String key){
        redisTemplate.delete( key);
    }

    /**
     * 获取所有的某前缀开头的key
     */
    public Set<String> keys(Prefix prefix){
        String base = "";
        if (!Objects.isNull(prefix)){
            base = prefix.getPrefix();
        }
        String keysParren = base + "*";
        Set keys = redisTemplate.keys( keysParren);
        return keys;
    }

    public Set<String> keys(String keysParam){
        String keysParren = keysParam + "*";
        Set keys = redisTemplate.keys( keysParren);
        return keys;
    }

    /**
     * 增减 1
     */
    public void incrByExpireTime(String key, int expireTime){
        valueOperations.increment(key);
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    public void incr(String key){
        valueOperations.increment(key);
    }

    public void decr(String key){
        valueOperations.decrement(key);
    }


    /**
     * 工具类，获取完整的key值
     * @param prefix
     * @param key
     */
    protected String getCurrentKey(Prefix prefix, String key){
        if (Objects.isNull(prefix)){
            return key;
        }
        String s = prefix.getPrefix();
        return s + key;
    }

    /**
     * 如果key不存在，则设置key 的值为 value. 存在则不设置
     * Boolean setIfAbsent(K key, V value);
     *
     * 把一个map的键值对添加到redis中，key-value 对应着 key value。如果key已经存在就覆盖
     * void multiSet(Map<? extends K, ? extends V> map);
     *
     * 把一个map的键值对添加到redis中，key-value 对应着 key value。 当且仅当map中的所有key都
     * 不存在的时候，添加成功返回 true，否则返回false.
     * Boolean multiSetIfAbsent(Map<? extends K, ? extends V> map);
     *
     * 根据提供的key集合按顺序获取对应的value值
     * List<V> multiGet(Collection<K> keys);
     *
     * 设置key的值为value 并返回旧值。 如果key不存在返回为null
     * V getAndSet(K key, V value);
     *
     * 为 key的值末尾追加 value 如果key不存在就直接等于 set(K key, V value)
     * Integer append(K key, String value);
     */


    /**
     * 【分布式锁】获取锁
     * @param key
     * @param value
     * @return
     */
    public Boolean lock (String key, String value) {
        return valueOperations.setIfAbsent(key, value, Duration.ofSeconds(30));
    }

    public Boolean lock (Prefix prefix, String key, String value) {
        return valueOperations.setIfAbsent(getCurrentKey(prefix, key), value, Duration.ofSeconds(prefix.expire()));
    }

    public Boolean unLock(String key, String value) {
        redisTemplate.execute(RedisScript.of(LUA_DELETE_LOCK, Long.class),
                Collections.singletonList(key),
                Collections.singletonList(value));
        return true;
    }

    public Boolean unLock(Prefix prefix, String key, String value) {
        redisTemplate.execute(RedisScript.of(LUA_DELETE_LOCK, Long.class),
                Collections.singletonList(getCurrentKey(prefix, key)),
                Collections.singletonList(value));
        return true;
    }
}
