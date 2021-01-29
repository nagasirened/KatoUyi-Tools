package com.katouyi.tools.utils;

import java.util.UUID;

/**
 * author: ZGF
 * context : UUID相关
 */

public class UUIDUtils {
    
    public static final String UUID_DELIMITER = "-";
    
    /**
     * 生成UUID，不替换分隔符
     */
    public synchronized static String generator() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成UUID，不替换分隔符，去掉分隔符
     */
    public static String generatorTransform(){
        return generator().replaceAll(UUID_DELIMITER, "");
    }
}
