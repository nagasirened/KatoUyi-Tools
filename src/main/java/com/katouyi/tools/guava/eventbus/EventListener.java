package com.katouyi.tools.guava.eventbus;


import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.Subscribe;

import java.util.Map;

/**
 *
 * 每个订阅的方法只能由一个参数，同时需要@Subscribe标记
 *
 */
public class EventListener {

    /**
     * 监听
     */
    @Subscribe
    public void listerInteger(Map<String, String> mapParam) {
        System.out.println( "Map param is: " + JSON.toJSONString(mapParam) );
    }

    /**
     * 监听String类型的事件
     */
    @Subscribe
    public void listerString(String param) {
        System.out.println( "String param is: " + param );
    }

}
