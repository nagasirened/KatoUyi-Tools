package com.katouyi.tools.exercise.common.util;

import cn.hutool.core.convert.Convert;
import com.katouyi.tools.exercise.common.constant.LoginUser;
import com.katouyi.tools.utils.ThreadLocalMap;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * token安全检测工具类
 *
 * @author pangu
 */
@Slf4j
public class SecurityUtil {

	public static final String HEADER_TOKEN = "token";

	/**
	 * 从HttpServletRequest里获取token
	 */
	public static String getToken(HttpServletRequest request) {
		String headerToken = request.getHeader(HEADER_TOKEN);
		if (StringUtils.isBlank(headerToken)) {
			throw new RuntimeException("没有携带Token信息！");
		}
		return StringUtils.isNotBlank(headerToken) ? TokenUtil.getToken(headerToken) : "";
	}

	/**
	 * 从Token解析获取Claims对象
	 *
	 * @param token Mate-Auth获取的token
	 * @return Claims
	 */
	public static Claims getClaims(String token) {
		Claims claims = null;
		if (StringUtils.isNotBlank(token)) {
			try {
				claims = TokenUtil.getClaims(token);
			} catch (Exception e) {
				throw new RuntimeException("Token已过期！");
			}
		}
		return claims;
	}

	/**
	 * 从HttpServletRequest获取LoginUser信息
	 */
	public static LoginUser getUsername(HttpServletRequest request) {
		String token = getToken(request);
		Claims claims = getClaims(token);
		// 然后根据token获取用户登录信息，这里省略获取用户信息的过程
		LoginUser loginUser = new LoginUser();
		loginUser.setUserId(String.valueOf(claims.get("userId")));
		loginUser.setAccount((String) claims.get("userName"));
		loginUser.setRoleId(String.valueOf(claims.get("roleId")));
		loginUser.setTenantId(String.valueOf(claims.get("tenantId")));
		loginUser.setType(Convert.toInt(String.valueOf(claims.get("type"))));
		ThreadLocalMap.put("loginUser", loginUser);
		return loginUser;
	}
}
