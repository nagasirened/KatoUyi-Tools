package com.katouyi.tools.netty.im.service;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceMemoryImpl implements UserService{

    private Map<String, String> userPasswordMap;
    {
        userPasswordMap = new ConcurrentHashMap<String, String>() {{
            put("zhangsan", "123456");
            put("lisi", "123456");
            put("wangwu", "123456");
            put("zhaoliu", "123456");
        }};
    }

    @Override
    public boolean login(String username, String password) {
        String pass = userPasswordMap.get(username);
        return StringUtils.equals(pass, password);
    }
}
