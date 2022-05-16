package com.katouyi.tools.opentracing;
/*

import com.alibaba.fastjson.JSON;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Aspect
@Component
@Order(-1)
public class CustomTraceAspect {

    @Autowired
    private Tracer tracer;

    @Autowired
    @Qualifier("skipPattern")
    private Pattern skipPattern;

    @Pointcut("execution(* com.ky..*.*(..))")
    public void servicePointcut() {
    }

    @Pointcut("bean(basedConfigurer)")
    public void basedConfigurer() {
    }

    @Pointcut("(servicePointcut())&&!basedConfigurer()")
    public void jaegerPoint() {
    }

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String[] whiteClassArray = new String[]{
            "com.test.XXXFilter",
    };
    private final String[] whitePackageNameArray = new String[]{"com.test.boot.utils"};

    */
/**
     * <p>是否打印详细日志</p>
     *//*

    private boolean isWhite(Method method, String targetName) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            return true;
        }
        if (StringUtils.startsWithAny(targetName, whitePackageNameArray)) {
            return true;
        }
        return StringUtils.equalsAnyIgnoreCase(targetName, whiteClassArray);
    }

    @Around("jaegerPoint()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object object = null;
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String methodName = method.getName();
        String targetName = methodSignature.getDeclaringTypeName();
        if (!isTraced(methodName, targetName, proceedingJoinPoint)) {
            return proceedingJoinPoint.proceed();
        }
        Span span = tracer.buildSpan(targetName + "." + methodName).start();
        //是否打印详细信息
        boolean detail = isWhite(method, targetName);
        String args = null;
        String result = null;
        //创建scope
        try (Scope scope = tracer.scopeManager().activate(span)) {
            args = detail ? "" : JSON.toJSONString(proceedingJoinPoint.getArgs());
            tracer.activeSpan().setTag("methodName", methodName);
            tracer.activeSpan().log("request:" + args);
            tracer.activeSpan().log("thread:" + Thread.currentThread().getName());
            makeUrl(methodName, targetName, proceedingJoinPoint);
            object = proceedingJoinPoint.proceed();
            result = detail ? "" : JSON.toJSONString(object);
            tracer.activeSpan().log("response:" + result);
        } catch (Throwable t) {
            if (!(t instanceof ServiceException)) {
                logger.error("targetName:{}, method:{}, param:{}, result:{}", targetName, methodName, args, result, t);
            }
            Map<String, Object> exceptionLogs = new LinkedHashMap<>(10);
            exceptionLogs.put("event", Tags.ERROR.getKey());
            exceptionLogs.put("request:", args);
            exceptionLogs.put("response:", result);
            exceptionLogs.put("error.object", t);
            span.log(exceptionLogs);
            Tags.ERROR.set(span, true);
            throw t;
        } finally {
            span.finish();
        }
        return object;
    }

    */
/**
     * <p>跳过指定URL</p>
     *//*

    protected boolean isTraced(String methodName, String targetName, ProceedingJoinPoint proceedingJoinPoint) {
        if (!isUserDefinedFilter(methodName, targetName)) {
            return true;
        }
        if (this.skipPattern == null) {
            return true;
        }
        HttpServletRequest request = (HttpServletRequest) proceedingJoinPoint.getArgs()[0];
        String url = request.getRequestURI().substring(request.getContextPath().length());
        return !this.skipPattern.matcher(url).matches();
    }

    private boolean isUserDefinedFilter(String methodName, String targetName) {
        return StringUtils.equalsIgnoreCase(methodName, "doFilter") && StringUtils.equalsIgnoreCase(targetName, "com.test.boot.utils.AbUtils");
    }

    private void makeUrl(String methodName, String targetName, ProceedingJoinPoint proceedingJoinPoint) {
        try {
            if (!isUserDefinedFilter(methodName, targetName)) {
                return;
            }
            HttpServletRequest request = (HttpServletRequest) proceedingJoinPoint.getArgs()[0];
            String url = request.getRequestURL().toString();
            tracer.activeSpan().log("url:" + url);
        } catch (Exception e) {
            logger.error("fail", e);
        }
    }
}
*/
