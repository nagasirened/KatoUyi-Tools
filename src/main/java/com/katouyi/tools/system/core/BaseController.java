package com.katouyi.tools.system.core;


import com.katouyi.tools.elasticSearch2.Pager;
import com.ky.common.exception.ServiceException;
import com.ky.common.lang.Environment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;


/**
 * 包名：com.taihe.web.core <br>
 * <p>
 * 功能:httpservlet基类封装
 * </p>
 *
 * @version:1.0 <br/>
 */
public class BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public <T> Result<T> build(T t) {
        return build(ErrorCode.OK, t);
    }

    public <T> Result<T> build(ErrorCode errorCode) {
        return build(errorCode, null);
    }

    public <T> Result<T> build(ErrorCode errorCode, T t) {
        return new Result<T>(errorCode.getCode(), errorCode.getMessage(), t);
    }

    public <T> Result<T> build(String message, T t) {
        return new Result<T>(ErrorCode.OK.getCode(), message, t);
    }

    public <T> Result<T> build(int errorCode, String message) {
        return new Result<T>(errorCode, message, null);
    }

    /**
     * <p>功能:获取分页对象</p>
     * <b>
     * 页码参数名: pageNumber 如未此传入则默认为第一页<br/>
     * 每页数量参数名:pageSize 如未传入则默认每页10条数据
     * </b>
     *
     * @param request
     * @return
     * @author:
     * @version:1.0 <br/>
     * <p>开发时间:2019/6/25 13:24</p>
     */
    public <T> Pager<T> getPager(HttpServletRequest request) throws ServiceException {
        String pageNumber = "0";
        String pageSize = "10";
        try {
            pageNumber = StringUtils.defaultIfBlank(request.getParameter("pageNumber"), "1");
            pageSize = StringUtils.defaultIfBlank(request.getParameter("pageSize"), "10");
        } catch (Exception e) {
            logger.error("base controller,get pager fail,pageNumber:{},pageSize:{},error message:{}", request.getParameter("pageNumber"), request.getParameter("pageSize"), e.getMessage(), e);
        }
        return getPager(Integer.parseInt(pageNumber), Integer.parseInt(pageSize));
    }

    /**
     * <p>功能:获取分页对象</p>
     *
     * @param pageNumber 页码
     * @param pageSize   每页数量
     * @return
     * @author:
     * @version:1.0 <br/>
     * <p>开发时间:2019/6/25 13:23</p>
     */
    public <T> Pager<T> getPager(Integer pageNumber, Integer pageSize) throws ServiceException {
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Pager<T> pager = new Pager<T>(pageNumber, pageSize);
        return pager;
    }

    /**
     * <p>功能:获取APP基础信息</p>
     *
     * @param
     * @return
     * @author:
     * @version:1.0 <br/>
     * <p>开发时间:2019/6/14 15:55</p>
     */
    public Environment getEnvironment(HttpServletRequest request) throws ServiceException {
        Environment environment = new Environment();
        environment.setPlatform(request.getHeader("platform"));
        environment.setPlatformVersion(request.getHeader("platform-v"));
        environment.setPlatformModel(request.getHeader("platform-model"));
        environment.setPlatformBrand(request.getHeader("platform-brand"));
        environment.setClientVersion(request.getHeader("client-v"));
        environment.setDeviceId(request.getHeader("device-id"));
        environment.setNetworkType(request.getHeader("network-type"));
        //        environment.setIp(getIPAddress(request));
        environment.setIp(request.getHeader("ip"));
        return environment;
    }

    private final static String UNKNOWN = "unknown";

    public static String getIPAddress(HttpServletRequest request) {
        String address = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getRemoteAddr();
        }
        if (StringUtils.isBlank(address)) {
            return address;
        }
        //IP去重复
        String[] ipArray = address.split(",");
        if (ipArray.length == 0 || ipArray.length == 1) {
            return address;
        }
        ArrayList<String> arrayList = new ArrayList<String>(2);
        for (String ip : ipArray) {
            ip = ip.trim();
            if (arrayList.contains(ip)) {
                continue;
            }
            arrayList.add(ip);
        }
        return StringUtils.join(arrayList, ",");
    }

    public boolean isAjax(HttpServletRequest request) {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return true;
        }
        return false;
    }
}
