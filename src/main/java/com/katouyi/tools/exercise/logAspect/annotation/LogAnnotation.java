package com.katouyi.tools.exercise.logAspect.annotation;


import com.katouyi.tools.exercise.logAspect.constant.LogTypeEnum;

import java.lang.annotation.*;

/**
 * 日志标记，可以用在方法上或者参数上
 */

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    /**
     * 日志类型，默认是操作日志
     */
    LogTypeEnum type() default LogTypeEnum.OPERATION;

    /**
     * 是否保存请求的参数
     *
     * @return the boolean
     */
    boolean isSaveRequestData() default false;

    /**
     * 是否保存响应的结果
     *
     * @return the boolean
     */
    boolean isSaveResponseData() default false;

}
