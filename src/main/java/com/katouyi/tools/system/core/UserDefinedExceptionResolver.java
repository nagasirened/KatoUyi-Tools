package com.katouyi.tools.system.core;

import com.alibaba.fastjson.JSON;
import com.ky.common.exception.AlarmLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserDefinedExceptionResolver extends DefaultHandlerExceptionResolver {
    private Logger logger = LoggerFactory.getLogger(UserDefinedExceptionResolver.class);

    /**
     * Sets the {@linkplain #setOrder(int) order} to {@link #LOWEST_PRECEDENCE}.
     */
    public UserDefinedExceptionResolver() {
        setOrder(Ordered.LOWEST_PRECEDENCE + 1);
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        return handler(request, response, e);
    }

    public ModelAndView handler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        String ip = HttpServletHelper.getIPAddress(request);
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        if (!(e instanceof ServiceException)) {
            AlarmLogger.error("please handler exception,ip:{},url:{},method:{},error message:{}", ip, url, method, e.getMessage());
            logger.error("system error,please handler exception,ip:{},url:{},method:{},error message:{}", ip, url, method, e.getMessage(), e);
        }
        if (e instanceof ServiceException) {
            ServiceException exception = (ServiceException) e;
            //系统异常需直接报警
            if (exception.getCode() == ErrorCode.SYSTEM_ERROR.getCode()) {
                AlarmLogger.error("please handler exception,ip:{},url:{},method:{},error message:{}", ip, url, method, e.getMessage());
            }
            response(request, response, exception.getCode(), exception.getMessage());
            return new ModelAndView();
        }
        if (e instanceof ServiceException) {
            ServiceException exception = (ServiceException) e;
            response(request, response, exception.getCode(), exception.getMessage());
            return new ModelAndView();
        }
        if (e instanceof MissingServletRequestParameterException && e.getMessage().toLowerCase().indexOf("required commonsmultipartfile parameter") != -1) {
            response(request, response, ErrorCode.REQUEST_FILE_IS_EMPTY);
            return new ModelAndView();
        }
        if (e instanceof BindException) {
            response(request, response, ErrorCode.PARAM_ERROR);
            return new ModelAndView();
        }
        if (e instanceof HttpRequestMethodNotSupportedException) {
            response(request, response, ErrorCode.REQUEST_METHOD_NOT_SUPPORTED);
            return new ModelAndView();
        }
        if (e instanceof HttpMediaTypeNotSupportedException) {
            response(request, response, ErrorCode.REQUEST_MEDIATYPE_NOT_SUPPORTED);
            return new ModelAndView();
        }
        if (e instanceof NoHandlerFoundException) {
            response(request, response, ErrorCode.REQUEST_NO_HANDLER_FOUND);
            return new ModelAndView();
        }
        if (e instanceof HttpMessageNotReadableException && StringUtils.containsIgnoreCase(e.getMessage(), "Required request body is missing")) {
            response(request, response, ErrorCode.PARAM_EMPTY);
            return new ModelAndView();
        }
        response(request, response, ErrorCode.SYSTEM_ERROR);
        return new ModelAndView();
    }

    /**
     * 提示失败信息
     *
     * @param message
     */
    public void response(HttpServletRequest request, HttpServletResponse response, int errorCode, String message) {
        HttpServletHelper.build(response, JSON.toJSONString(new Result(errorCode, message)));
    }

    /**
     * 提示失败信息
     */
    public void response(HttpServletRequest request, HttpServletResponse response, ErrorCode errorCode) {
        response(request, response, errorCode.getCode(), errorCode.getMessage());
    }
}
