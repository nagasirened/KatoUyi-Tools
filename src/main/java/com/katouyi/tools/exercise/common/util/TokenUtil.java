package com.katouyi.tools.exercise.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Base64;

/**
 * Token工具类
 *
 * @author pangu
 */
public class TokenUtil {

	public static String SIGN_KEY = "MATE";
	public static String BEARER = "bearer";
	public static Integer AUTH_LENGTH = 7;

	/**
	 * 获取token串
	 */
	public static String getToken(String auth) {
		if ((auth != null) && (auth.length() > AUTH_LENGTH)) {
			String headStr = auth.substring(0, 6).toLowerCase();
			if (headStr.compareTo(BEARER) == 0) {
				auth = auth.substring(7);
			}
			return auth;
		}
		return null;
	}

	/**
	 * 获取jwt中的claims信息
	 */
	public static Claims getClaims(String token) {
		String key = Base64.getEncoder().encodeToString(SIGN_KEY.getBytes());
		return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
	}
}
