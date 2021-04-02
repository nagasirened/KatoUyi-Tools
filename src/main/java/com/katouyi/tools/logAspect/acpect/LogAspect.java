/*
package com.katouyi.tools.logAspect.acpect;

import com.katouyi.constant.wrapper.Wrapper;
import com.katouyi.utils.PubUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Date;

*/
/**
 * 处理日志
 *//*


@Slf4j
@Aspect
@Component
public class LogAspect {

    private ThreadLocal threadLocal = new ThreadLocal<Date>();

    @Pointcut("@annotation(com.katouyi.annotation.LogAnnotation)")
    public void cut(){};

    @Around("cut()")
    public Object around(final ProceedingJoinPoint joinPoint) {
        threadLocal.set(new Date());
        Object result = null;
        try {
            result = joinPoint.proceed();
            if (result instanceof Wrapper) {
                Wrapper wrapper = (Wrapper) result;
                // 成功返回
                if (!PubUtils.isNull(wrapper) && wrapper.getCode() == Wrapper.SUCCESS_CODE) {
                    handleLog(joinPoint, wrapper);
                }
            }
        } catch (Throwable throwable) {
            log.error("日志注解出现异常，{}", throwable.getMessage(), throwable);
        }
        return result;
    }

    private void handleLog(ProceedingJoinPoint joinPoint, Wrapper wrapper) {

    }
}
*/
