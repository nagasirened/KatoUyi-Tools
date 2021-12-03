package com.katouyi.tools.mate.common.method;

/**
 * 环境常量
 */

public enum EnvType {

	/**
	 * 环境变量
	 * LOCAL 本地
	 * DEV 开发
	 * TEST 测试
	 * PROD 生产
	 * DOCKER Docker
	 */
	LOCAL("local"),
	DEV("dev"),
	TEST("test"),
	PROD("prod"),
	DOCKER("docker");

	private final String value;

	EnvType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
