package com.katouyi.tools.netty.im.session;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SessionMemoryImpl implements Session {

    // 用户 -> channel
    private final Map<String, Channel> userChannelMap = new ConcurrentHashMap<>();
    // channel -> 用户
    private final Map<Channel, String> channelUserMap = new ConcurrentHashMap<>();
    // channel -> 参数字典
    private final Map<Channel, Map<String, Object>> channelParamMap = new ConcurrentHashMap<>();

    @Override
    public void bind(Channel channel, String username) {
        userChannelMap.put(username, channel);
        channelUserMap.put(channel, username);
        channelParamMap.put(channel, new ConcurrentHashMap<>());
    }

    @Override
    public void unbind(Channel channel) {
        channelParamMap.remove(channel);
        String username = channelUserMap.remove(channel);
        userChannelMap.remove(username);
    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        Map<String, Object> params = channelParamMap.getOrDefault(channel, new ConcurrentHashMap<>());
        return params.get(name);
    }

    @Override
    public void setAttribute(Channel channel, String name, Object value) {
        Map<String, Object> params = channelParamMap.get(channel);
        if (Objects.isNull(params)) {
            params = new ConcurrentHashMap<>();
            params.put(name, value);
        }
    }

    @Override
    public Channel getChannel(String username) {
        return userChannelMap.get(username);
    }
}
