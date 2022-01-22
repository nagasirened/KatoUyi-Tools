package com.katouyi.tools.netty.im.config;

import cn.hutool.core.convert.Convert;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Properties;

public class SerializerConfig {
    static Properties properties;
    static {
        try (InputStream in = SerializerConfig.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static int getServerPort() {
        String serverPort = properties.getProperty("server.port");
        if (StringUtils.isBlank(serverPort)) {
            return 8080;
        }
        return Convert.toInt(serverPort);
    }

    /**
     * 可配置使用不同的序列化算法
     */
    public static Serializer.Algorithm getSerializeAlgorithm() {
        String serializeAlgorithm = properties.getProperty("serialize.algorithm");
        if (StringUtils.isBlank(serializeAlgorithm)) {
            return Serializer.Algorithm.JAVA;
        } else {
            return Serializer.Algorithm.valueOf(StringUtils.upperCase(serializeAlgorithm));
        }
    }

}
