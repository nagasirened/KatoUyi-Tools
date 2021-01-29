/*
 * Copyright (c) 2018. paascloud.net All Rights Reserved.
 * 项目名称：paascloud快速搭建企业级分布式微服务平台
 * 类名称：RandomUtil.java
 * 创建人：刘兆明
 * 联系方式：paascloud.net@gmail.com
 * 开源地址: https://github.com/paascloud
 * 博客地址: http://blog.paascloud.net
 * 项目官网: http://paascloud.net
 */

package com.katouyi.tools.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomUtil {

	/**
	 * 随机验证码的长度最大为20
	 */
	private static final int MAX_LENGTH = 20;

	/**
	 * 生成一个随机验证码 大小写字母+数字
	 * @param length 随机验证码的长度
	 * @return 随机验证码 string
	 */
	public static String createComplexCode(int length) {
		length = length > MAX_LENGTH ? MAX_LENGTH : length;
		Random random = new Random();
		StringBuilder code = new StringBuilder();
		while (true) {
			if (code.length() == length) {
				break;
			}
			int tmp = random.nextInt(127);
			if (tmp < 33 || tmp == 92 || tmp == 47 || tmp == 34) {
				continue;
			}
			char x = (char) (tmp);
			if (code.toString().indexOf(x) > 0) {
				continue;
			}
			code.append(x);
		}
		return code.toString();
	}

	/**
	 * 生成一个随机字符串ID
	 * @param length 随机验证码的长度
	 * @return the string
	 */
	public static String createNumberCode(int length) {
		String numberCode = randomString("0123456789", length);
		if (numberCode.startsWith("0")) {
			return numberCode.substring(1) + "0";
		}
		return numberCode;
	}

	/**
	 * 从遗传字符串中选择字符，并随机拼接
	 * @param baseString
	 * @param length
	 * @return
	 * 示例：RandomUtil.randomString("abcdefghijklmn",10)   ===>  gialelilnm
	 */
	private static String randomString(String baseString, int length) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		if (length < 1) {
			length = 1;
		}

		int baseLength = baseString.length();
		for (int i = 0; i < length; ++i) {
			int number = random.nextInt(baseLength);
			sb.append(baseString.charAt(number));
		}

		return sb.toString();
	}
}
