package com.katouyi.tools.redis.prefix;

/**
 * @description: 公共的抽象类
 * @author: ZengGuangfu
 */
public abstract class BasePrefix implements Prefix {

    private int expireTime;

    private String prefixStr;

    public BasePrefix(int expireTime, String prefixStr){
        this.expireTime = expireTime;
        this.prefixStr = prefixStr;
    }

    public BasePrefix(String prefixStr){
        this(0,prefixStr);
    }

    @Override
    public int expire() {
        return expireTime;
    }

    @Override
    public void setExpire(int expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String getPrefix() {
        return prefixStr;
    }
}
