package com.katouyi.tools.redis.prefix;

/**
 * @description: 存储在redis中的前缀
 * @author: ZengGuangfu
 */
public class CookieTokenPrefix extends BasePrefix {

    public CookieTokenPrefix(int expire, String prefix){
        super(expire, prefix);
    }

    public static CookieTokenPrefix COOKIE_TOKEN = new CookieTokenPrefix(1800,"login_token:");
}
