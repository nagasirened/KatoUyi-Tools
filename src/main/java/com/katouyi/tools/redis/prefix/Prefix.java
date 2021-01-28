package com.katouyi.tools.redis.prefix;

public interface Prefix {

    public int expire();

    public void setExpire(int expire);

    public String getPrefix();
}
