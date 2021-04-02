package com.katouyi.tools.system.core;

import com.alibaba.fastjson.JSON;
import com.ky.common.entity.Result;
import com.ky.common.lang.Environment;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServletHelper {
    private static Logger logger = LoggerFactory.getLogger(HttpServletHelper.class);
    private static final String UNKNOWN = "unknown";

    public HttpServletHelper() {
    }

    public static String getAttribute(HttpServletRequest request, String key) {
        if (StringUtils.isEmpty(key)) {
            return "";
        } else {
            Object attribute = request.getAttribute(key);
            return attribute == null ? "" : attribute.toString();
        }
    }

    public static void setAttribute(HttpServletRequest request, String key, String value) {
        if (!StringUtils.isEmpty(key)) {
            request.setAttribute(key, value);
        }
    }

    public static boolean isAjax(HttpServletRequest request) {
        return StringUtils.equals(request.getHeader("X-Requested-With"), "XMLHttpRequest");
    }

    public static void delCookie(HttpServletResponse response, String key) {
        try {
            Cookie name = new Cookie(key, (String)null);
            name.setMaxAge(0);
            response.addCookie(name);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static void addCookie(HttpServletResponse response, String key, String value) {
        try {
            Cookie name = new Cookie(key, URLEncoder.encode(value, "UTF-8"));
            name.setMaxAge(86400);
            response.addCookie(name);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public static String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        } else {
            Cookie[] var3 = cookies;
            int var4 = cookies.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Cookie cookie = var3[var5];
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }

            return null;
        }
    }

    public static String getIPAddress(HttpServletRequest request) {
        String address = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(address) || "unknown".equalsIgnoreCase(address)) {
            address = request.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isBlank(address) || "unknown".equalsIgnoreCase(address)) {
            address = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isBlank(address) || "unknown".equalsIgnoreCase(address)) {
            address = request.getHeader("HTTP_CLIENT_IP");
        }

        if (StringUtils.isBlank(address) || "unknown".equalsIgnoreCase(address)) {
            address = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (StringUtils.isBlank(address) || "unknown".equalsIgnoreCase(address)) {
            address = request.getRemoteAddr();
        }

        if (StringUtils.isBlank(address)) {
            return address;
        } else {
            String[] ipArray = address.split(",");
            if (ipArray.length != 0 && ipArray.length != 1) {
                ArrayList<String> arrayList = new ArrayList(2);
                String[] var4 = ipArray;
                int var5 = ipArray.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    String ip = var4[var6];
                    ip = ip.trim();
                    if (!arrayList.contains(ip)) {
                        arrayList.add(ip);
                    }
                }

                return StringUtils.join(arrayList, ",");
            } else {
                return address;
            }
        }
    }

    public static void build(HttpServletResponse response, Object data) {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        try {
            response.getWriter().write(data != null ? data.toString() : "");
            response.getWriter().flush();
            response.getWriter().close();
        } catch (Exception var3) {
            logger.error("http servlet,build fail,error message:{}", var3.getMessage(), var3);
        }

    }

    public static void build(HttpServletRequest request, HttpServletResponse response, Result result) {
        Environment environment = getEnvironment(request);
        // String message = MessageHelper.get(environment.getLan(), result.getCode());
        String message = "XXXXXX";
        String data = JSON.toJSONString(new Result(result.getCode(), message));
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        try {
            response.getWriter().write(data != null ? data : "");
            response.getWriter().flush();
            response.getWriter().close();
        } catch (Exception var7) {
            logger.error("http servlet,build fail,error message:{}", var7.getMessage(), var7);
        }

    }

    private static String getIpAddr(HttpServletRequest request, String header) {
        String ip = request.getHeader(header);
        return !StringUtils.isEmpty(ip) && !StringUtils.equalsIgnoreCase("unknown", ip) ? ip : "";
    }

    public static Environment getEnvironment(HttpServletRequest request) {
        Environment environment = new Environment();
        environment.setPlatform(request.getHeader("platform"));
        environment.setPlatformVersion(request.getHeader("platform-v"));
        environment.setPlatformModel(request.getHeader("platform-model"));
        environment.setPlatformBrand(request.getHeader("platform-brand"));
        environment.setClientVersion(request.getHeader("client-v"));
        environment.setDeviceId(request.getHeader("device-id"));
        environment.setNetworkType(request.getHeader("network-type"));
        environment.setIp(getIPAddress(request));
        environment.setLan(request.getHeader("lan"));
        environment.setToken(request.getHeader("token"));
        environment.setSid(request.getHeader("sid"));
        return environment;
    }

    public static HttpServletResponse setCookie(HttpServletResponse response, String name, String value, int time) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(time);
        response.addCookie(cookie);
        response.setHeader(name, value);
        return response;
    }

    public static void setSessionValue(HttpServletRequest request, String key, String value) {
        HttpSession session = request.getSession();
        session.setAttribute(key, value);
    }

    public static Map<String, String> getDevice(HttpServletRequest request) {
        try {
            Map<String, String> map = new HashMap();
            map.put("ip", getIPAddress(request));
            map.put("platform", isIOS(request) ? "ios" : "android");
            map.put("userAgent", request.getHeader("user-agent"));
            return map;
        } catch (Exception var2) {
            return new HashMap();
        }
    }

    public static Boolean isIOS(HttpServletRequest request) {
        String agent = request.getHeader("user-agent");
        return !agent.contains("Android") && !agent.contains("android") ? true : false;
    }

}
