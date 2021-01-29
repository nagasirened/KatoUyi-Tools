package com.katouyi.tools.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadLocalMap {

	/**
	 * 拓展ThreadLocal，存储类型默认为Map
	 */
	private final static ThreadLocal<Map<String, Object>> THREAD_CONTEXT = new MapThreadLocal();

	private static class MapThreadLocal extends ThreadLocal<Map<String, Object>> {
		/**
		 * 定义Thread 中存储Map结构
		 * @return the map
		 */
		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>(8) {
				private static final long serialVersionUID = 3637958959138295593L;
				@Override
				public Object put(String key, Object value) {
					return super.put(key, value);
				}
			};
		}
	}

	private static Map<String, Object> getContextMap() {
		return THREAD_CONTEXT.get();
	}

	/**
	 * 新增键值对
	 */
	public static void put(String key, Object value) {
		getContextMap().put(key, value);
	}

	/**
	 * 获取内容
	 */
	public static Object get(String key) {
		return getContextMap().get(key);
	}

	/**
	 * 移除Key
	 */
	public static Object remove(String key) {
		return getContextMap().remove(key);
	}

	/**
	 * 清理线程所有存储的对象
	 */
	public static void removeCurrent() {
		getContextMap().clear();
	}
}