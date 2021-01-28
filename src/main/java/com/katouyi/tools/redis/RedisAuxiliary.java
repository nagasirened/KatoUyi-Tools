package com.katouyi.tools.redis;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.katouyi.tools.redis.prefix.Prefix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @description: redis操作工具
 * </p>
 * @author: ZengGuangfu
 */

@Slf4j
@Component
public class RedisAuxiliary {

    @Autowired
    private ValueOperations<String, Object> valueOperations;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * LUA脚本释放
     * 分布式锁使用
     * 涉及方法lock unlock 等
     */
    final String LUA_DELETE_LOCK = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

    /**
     * 存储，没有时间限制
     */
    public void setWithoutExpire(Prefix prefix, String key, Object value){
        valueOperations.set(getCurrentKey(prefix, key), value);
    }

    public void setWithoutExpire(String key, Object value){
        valueOperations.set(key, value);
    }

    /**
     * 存储，按照设定的时间限制，单位为秒
     */
    public void setWithExpire(Prefix prefix, String key ,Object value){
        valueOperations.set( getCurrentKey(prefix, key), value, prefix.expire(), TimeUnit.SECONDS);
    }

    public void setWithExpire(String key ,Object value, long expireTime){
        valueOperations.set(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取对象
     */
    public Object get(Prefix prefix, String key){
        return valueOperations.get(getCurrentKey(prefix, key));
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
     * 删除对象
     */
    public void delete(Prefix prefix, String key){
        redisTemplate.delete(getCurrentKey(prefix, key));
    }

    public void delete(String key){
        redisTemplate.delete( key);
    }

    public void delete(Prefix prefix, Collection<String> keys){
        Set<String> ketSet = keys
                .stream()
                .map(item -> getCurrentKey(prefix, item))
                .collect(Collectors.toSet());
        redisTemplate.delete(ketSet);
    }

    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 是否存在key
     */
    public Boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    public Boolean hasKey(Prefix prefix, String key){
        return redisTemplate.hasKey(getCurrentKey(prefix, key));
    }

    /**
     * 查找匹配的Key
     */
    public Set<String> patternKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public Set<String> prefixKeys(Prefix prefix){
        String base = "";
        if (!Objects.isNull(prefix)){
            base = prefix.getPrefix();
        }
        String keysParren = base + "*";
        Set keys = redisTemplate.keys( keysParren);
        return keys;
    }

    /**
     * 移除过期时间
     */
    public Boolean persist(String key){
        return redisTemplate.persist(key);
    }

    public Boolean persist(Prefix prefix,String key){
        return redisTemplate.persist(getCurrentKey(prefix, key));
    }

    /**
     * 查看过期时间
     */
    public Long getTTL(String key){
        return redisTemplate.getExpire(key);
    }

    public Long getTTL(Prefix prefix, String key){
        return redisTemplate.getExpire(getCurrentKey(prefix, key));
    }

    /**
     * 修改key的名称
     */
    public void rename(String oldKey, String newKey){
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 修改key的名称
     */
    public void rename(Prefix prefix, String oldKey, String newKey){
        redisTemplate.rename(getCurrentKey(prefix, oldKey), getCurrentKey(prefix, newKey));
    }

    /**
     * getAndSet 获取旧值，设置新值
     */
    public Object getAndSet(Prefix prefix, String key, Object value) {
        return valueOperations.getAndSet(getCurrentKey(prefix, key), value);
    }

    /**
     * 批量获取
     */
    public List<Object> mutliGet(Collection<String> keys){
        List<Object> objects = valueOperations.multiGet(keys);
        return objects;
    }

    public List<Object> mutliGet(Prefix prefix, Collection<String> keys){
        Set<String> ketSet = keys
                .stream()
                .map(item -> getCurrentKey(prefix, item))
                .collect(Collectors.toSet());
        List<Object> objects = valueOperations.multiGet(ketSet);
        return objects;
    }

    /**
     * 批量添加
     */
    public void multiSet(Prefix prefix, Map<String, Object> map) {
        HashMap<String, Object> keyMap = Maps.newHashMap();
        map.keySet().forEach(item -> {
            keyMap.put(getCurrentKey(prefix, item), map.get(item));
        });
        valueOperations.multiSet(keyMap);
    }

    public void multiSet( Map<String, Object> map) {
        valueOperations.multiSet(map);
    }

    /**
     * 增减 1
     */
    public void incr(String key){
        valueOperations.increment(key);
    }

    public void decr(String key){
        valueOperations.decrement(key);
    }

    public void incrByExpireTime(String key, int expireTime){
        valueOperations.increment(key);
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
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
     * ====================================分布式锁==========================================
     *
     *
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
