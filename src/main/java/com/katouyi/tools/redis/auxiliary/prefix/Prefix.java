package com.katouyi.tools.redis.auxiliary.prefix;

public interface Prefix {

    public int expire();

    public void setExpire(int expire);

    public String getPrefix();
}
