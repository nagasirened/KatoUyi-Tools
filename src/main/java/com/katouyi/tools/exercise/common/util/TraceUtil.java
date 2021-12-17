package com.katouyi.tools.exercise.common.util;

import jodd.util.StringUtil;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;

/**
 * 链路追踪工具类
 *
 * @author xuzhanfu
 */
public class TraceUtil {

    /**
     * 从header和参数中获取traceId
     * 从前端传入数据
     */
    public static String getTraceId(HttpServletRequest request) {
        String traceId = request.getParameter("traceId");
        if (StringUtil.isBlank(traceId)) {
            traceId = request.getHeader("traceId");
        }
        return traceId;
    }

    /**
     * 传递traceId至MDC
     * @param traceId　跟踪ID
     */
    public static void mdcTraceId (String traceId) {
        if (StringUtil.isNotBlank(traceId)) {
            MDC.put("traceId", traceId);
        }
    }
}
