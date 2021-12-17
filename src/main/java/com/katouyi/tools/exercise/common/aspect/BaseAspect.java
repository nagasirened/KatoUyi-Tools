package com.katouyi.tools.exercise.common.aspect;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.JoinPoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Aspect 基础类
 */
public abstract class BaseAspect {

    /**
     * 从方法上获取指定的注解对象信息
     */
    public <A extends Annotation> A getAnnotation(JoinPoint joinPoint, Class<A> annotationClass) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(annotationClass);
    }

    /**
     * 获取方法基础信息
     * @return 类路径、方法名、参数
     */
    public MethodBaseInfo getMethodBaseInfo(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        String targetName = signature.getDeclaringTypeName();
        String methodName = method.getName();
        Object[] arguments = joinPoint.getArgs();
        return new MethodBaseInfo(targetName, methodName, arguments);
    }

    /**
     * 封装方法信息的类
     */
    public static class MethodBaseInfo {
        private final String targetName;
        private final String methodName;
        private final Object[] arguments;

        public MethodBaseInfo(String targetName, String methodName, Object[] arguments) {
            this.targetName = targetName;
            this.methodName = methodName;
            this.arguments = arguments;
        }

        public String getTargetName() {
            return targetName;
        }

        public String getMethodName() {
            return methodName;
        }

        public Object[] getArguments() {
            return arguments;
        }
    }

    /**
     * 拼接缓存Key
     */
    public String getCacheKey(String key, MethodBaseInfo methodBaseInfo) {
        return getCacheKey(key, methodBaseInfo.getTargetName(), methodBaseInfo.getMethodName(), methodBaseInfo.getArguments());
    }

    /**
     * 拼接缓存Key
     *
     * key存在则使用key, key不存在则使用 targetName.methodName
     */
    public String getCacheKey(String key, String targetName, String methodName, Object[] arguments) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNoneBlank(key)) {
            sb.append(key);
        } else {
            sb.append(targetName).append(".").append(methodName);
        }

        if (Objects.nonNull(arguments) && arguments.length > 0) {
            sb.append(StrUtil.COLON).append(JSON.toJSONString(arguments));
        }
        return sb.toString().replace("[", "")
                            .replace("\"", "")
                            .replace("]", "");
    }

}
