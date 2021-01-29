/*
 * Copyright (c) 2018. paascloud.net All Rights Reserved.
 * 项目名称：paascloud快速搭建企业级分布式微服务平台
 * 类名称：Collections3.java
 * 创建人：刘兆明
 * 联系方式：paascloud.net@gmail.com
 * 开源地址: https://github.com/paascloud
 * 博客地址: http://blog.paascloud.net
 * 项目官网: http://paascloud.net
 */

package com.katouyi.tools.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Collections3Utils {

	/**
	 * 求并集，但是不去重
	 * @param a
	 * @param b
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> union(final Collection<T> a, final Collection<T> b) {
		List<T> result = new ArrayList<>(a);
		result.addAll(b);
		return result;
	}

	/**
	 * 求并集，去重
	 * @param a
	 * @param b
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> unionAndDeduplication(final Collection<T> a, final Collection<T> b) {
		ArrayList<T> result = new ArrayList<>(a);
		for (T element : b) {
			if (!result.contains(element)) {
				result.add(element);
			}
		}
		return result;
	}

	/**
	 * 获取A与B的差集
	 * @param a
	 * @param b
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> subtract(final Collection<T> a, final Collection<T> b) {
		List<T> list = new ArrayList<>(a);
		for (T element : b) {
			list.remove(element);
		}

		return list;
	}

	/**
	 * 获取A与B的交集（A和B都存在）
	 * @param a
	 * @param b
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
		List<T> list = new ArrayList<>();
		for (T element : a) {
			if (b.contains(element)) {
				list.add(element);
			}
		}
		return list;
	}

}
