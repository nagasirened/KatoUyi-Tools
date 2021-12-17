package com.katouyi.tools.exercise.common.method;

/**
 * 方法类型
 */
public enum MethodTypeEnum {

	GET(false),
	PUT(true),
	POST(true),
	DELETE(false),
	HEAD(false),
	OPTIONS(false);

	private final boolean hasContent;

	MethodTypeEnum(boolean hasContent) {
		this.hasContent = hasContent;
	}

	public boolean isHasContent() {
		return hasContent;
	}
}
