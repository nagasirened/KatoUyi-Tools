package com.katouyi.tools.system.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import java.io.IOException;

public class UserDefinedFilter implements Filter {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            MDC.put("code", String.valueOf(ErrorCode.OK.getCode()));
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("user defined filter,do filter fail,error message:{}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }
}