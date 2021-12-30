package com.katouyi.tools.mongo.mall;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoField {

    /**
     * 拼接条件的ENUM的函数
     */
    MongoType type() default MongoType.EQUALS;

    /**
     * 字段别名
     */
    String  param() default "";

    /**
     * 查询时，是否需要排除该字段
     */
    boolean exclude() default false;
}
