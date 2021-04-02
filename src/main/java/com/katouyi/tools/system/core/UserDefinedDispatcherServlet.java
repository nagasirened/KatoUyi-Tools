package com.katouyi.tools.system.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class UserDefinedDispatcherServlet extends DispatcherServlet {
    Logger logger = LoggerFactory.getLogger(UserDefinedDispatcherServlet.class);
    private static final long serialVersionUID = 1L;

    public String findUserId(HttpServletRequest request) {
        return request.getHeader("uid");
    }

    @Override
    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response(request, response);
    }

    private void response(HttpServletRequest request, HttpServletResponse response) {
        if (request.getRequestURI().equalsIgnoreCase("/healthz")) {
            build(response, "ok");
            return;
        }
        response.setCharacterEncoding("UTF-8");
        Result result = new Result(ErrorCode.REQUEST_NO_HANDLER_FOUND.getCode(), ErrorCode.REQUEST_NO_HANDLER_FOUND.getMessage());
        if (StringUtils.endsWithIgnoreCase(request.getRequestURI(), "/favicon.ico") || StringUtils.equalsIgnoreCase(request.getRequestURI(), "/")) {
            return;
        }

        logger.error("request uri not found,uid:{},url:{},method:{},env:{}", findUserId(request), request.getRequestURI(), request.getMethod(), HttpServletHelper.getEnvironment(request));
        // AlarmLogger.error("request uri not found,uid:{},url:{},method:{},env:{}", findUserId(request), request.getRequestURI(), request.getMethod(), HttpServletHelper.getEnvironment(request));
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.write(JSON.toJSONString(result, SerializerFeature.WriteDateUseDateFormat));
        } catch (Exception e) {
            logger.error("User Defined DispatcherServlet response write result fail,result:{},errMsg:{},error:{}", JSON.toJSONString(result), e.getMessage(), e);
        }
    }

    public void build(HttpServletResponse response, String data) {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.write(data);
        } catch (Exception e) {
            logger.error("base interceptor fail,result:{},error message:{}", JSON.toJSONString(data), e.getMessage(), e);
        }
    }
}
