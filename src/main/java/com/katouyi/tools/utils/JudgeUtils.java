/*
 * Copyright (c) 2018. paascloud.net All Rights Reserved.
 * 项目名称：paascloud快速搭建企业级分布式微服务平台
 * 类名称：PubUtils.java
 * 创建人：刘兆明
 * 联系方式：paascloud.net@gmail.com
 * 开源地址: https://github.com/paascloud
 * 博客地址: http://blog.paascloud.net
 * 项目官网: http://paascloud.net
 */

package com.katouyi.tools.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JudgeUtils {

	public static final String PARAM_EXECEPTION_INFO = "参数异常";

	/**
	 * 匹配手机号码, 支持+86和86开头
	 */
	private static final String REGX_MOBILENUM = "^((\\+86)|(86))?(13|14|15|16|17|18|19)\\d{9}$";

	/**
	 * 匹配邮箱帐号
	 */
	private static final String REGX_EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

	/**
	 * 匹配手机号码
	 */
	public static Boolean isMobile(String value) {
		return StringUtils.isNotEmpty(value) && value.matches(REGX_MOBILENUM);
	}

	/**
	 * 验证是否是正确的邮箱
	 */
	public static boolean isEmail(String str) {
		if (StringUtils.isEmpty(str) || !str.matches(REGX_EMAIL)) {
			return false;
		}
		return true;
	}

	/**
	 * 如果传入的对象中，有一个为null，就直接抛出异常
	 * @param arg
	 */
	public static void hasNullAndThrow(Object... arg){
		for (int i = 0; i < arg.length; i++) {
			if (Objects.isNull(arg[i])){
				throw new RuntimeException(PARAM_EXECEPTION_INFO);
			}
		}
	}

	public static Boolean hasNull(Object... arg){
		for (int i = 0; i < arg.length; i++) {
			if (Objects.isNull(arg[i])){
				return true;
			}
		}
		return false;
	}

	public static void hasEmptyAndThrow(String... arg){
		for (int i = 0; i < arg.length; i++) {
			if (StringUtils.isEmpty(arg[i])){
				throw new RuntimeException(PARAM_EXECEPTION_INFO);
			}
		}
	}

	public static Boolean hasEmpty(String... arg){
		for (int i = 0; i < arg.length; i++) {
			if (StringUtils.isEmpty(arg[i])){
				return true;
			}
		}
		return false;
	}
}
