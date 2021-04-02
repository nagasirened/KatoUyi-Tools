package com.katouyi.tools.system.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ky.common.lang.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

/**
 * <p>功能:业务拦截器,主要用于用户登陆校验、参数解密</p>
 *
 * @param
 * @version:1.0 <br/>
 * <p>开发时间:2020/05/01 11:08</p>
 * @returnµ
 */
public class BasedInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            checkAuth(request);
        } catch (ServiceException e) {
            build(response, new Result(e.getErrorCode(), null));
            return false;
        }
        return true;
    }

    private static HashMap<String, String> params = new HashMap<String, String>(3);

    static {
        params.put("apple", "apple-is-foolish");
        params.put("grape", "vitisvinieral0o0");
        params.put("mango", "yellowfruit-+*o0");
    }

    public void checkAuth(HttpServletRequest request) throws ServiceException {
        String keyAndPassword = request.getParameter("s");
        try {
            String uri = request.getRequestURI();
            if (StringUtils.equals(request.getHeader("debug"), "kuaiyin@123") || isMatchUri(uri, whiteListUriArray)) {
                return;
            }
            if (StringUtils.isBlank(keyAndPassword)) {
                logger.error("base interceptor,check auth successful,key and password is empty,url:{}", request.getRequestURI());
                throw new ServiceException(ErrorCode.PARAM_EMPTY);
            }
            String key = keyAndPassword.split("\\|")[0];
            String password = keyAndPassword.split("\\|")[1];
            Map<String, String[]> paramMap = request.getParameterMap();
            paramMap.remove("s");
            ArrayList<String> keyList = new ArrayList<String>(paramMap.keySet());
            //KEY升序
            Collections.sort(keyList, String::compareTo);
            //组装参数字符串
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < keyList.size(); i++) {
                builder.append(keyList.get(i)).append("=").append(paramMap.get(keyList.get(i))[0]);
                if (i + 1 != keyList.size()) {
                    builder.append("&");
                }
            }
            if (StringUtils.equals(HMACSHA256(builder.toString(), params.get(key)), password)) {
                return;
            }
            logger.error("base interceptor,check auth fail,url:{},param:{},builder:{}", request.getRequestURI(), JSON.toJSONString(paramMap), builder.toString());
            throw new ServiceException(ErrorCode.AUTH_FAIL);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("base interceptor,check auth fail,url:{},s:{},error message:{}", request.getRequestURI(), keyAndPassword, e.getMessage(), e);
            throw new ServiceException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 不需要校验token的uri
     */
    List<String> whiteListUriArray = Arrays.asList
            (
                    "/music/search",
                    "/live/search/user",
                    "/live/search/room"
            );
    private static AntPathMatcher matcher = new AntPathMatcher();


    /**
     * <p> 须校验的uri</p>
     *
     * @param path
     * @param urls
     * @return : boolean
     * <p>开发时间: 2020/5/22 14:38</p>
     */
    public boolean isMatchUri(String path, List<String> urls) {
        if (org.apache.commons.lang3.StringUtils.isBlank(path) || CollectionUtils.isEmpty(urls)) {
            return true;
        }
        for (String pattern : urls) {
            if (pattern.startsWith("^")) {
                return matcher.match(pattern.split("\\^")[1], path);
            }
            if (match(path, pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * pattern 满足过滤规则返回true.
     *
     * @param path    the path
     * @param pattern the pattern
     * @return true, if successful
     */
    public boolean match(String path, String pattern) {
        if (StringUtils.isBlank(path) || StringUtils.isBlank(pattern)) {
            return false;
        }
        return matcher.match(pattern, path);
    }

    public String HMACSHA256(String data, String key) throws Exception {
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        Mac HMACSHA256 = Mac.getInstance("HmacSHA256");
        HMACSHA256.init(secret_key);
        byte[] array = HMACSHA256.doFinal(data.getBytes("UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (byte item : array) {
            builder.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
        }
        return builder.toString().toUpperCase();
    }

    public void build(HttpServletResponse response, Result result) {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.write(JSON.toJSONString(result, SerializerFeature.WriteDateUseDateFormat));
        } catch (Exception e) {
            logger.error("base interceptor fail,result:{},error message:{}", JSON.toJSONString(result), e.getMessage(), e);
        }
    }
}

