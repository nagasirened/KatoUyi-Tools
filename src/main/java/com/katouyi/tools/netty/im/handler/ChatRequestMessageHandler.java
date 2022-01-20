package com.katouyi.tools.netty.im.handler;

import com.katouyi.tools.netty.im.message.ChatRequestMessage;
import com.katouyi.tools.netty.im.message.ChatResponseMessage;
import com.katouyi.tools.netty.im.service.UserServiceFactory;
import com.katouyi.tools.netty.im.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 处理聊天请求，是把聊天信息发送给要发送的人
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String content = msg.getContent();
        if (StringUtils.isEmpty(content)) {
            ctx.writeAndFlush(new ChatResponseMessage(false, "内容为空"));
        }

        String to = msg.getTo();
        String from = msg.getFrom();
        if (StringUtils.isAnyEmpty(to, from)) {
            ctx.writeAndFlush(new ChatResponseMessage(false, "账号异常"));
        }
        // 获取toChannel
        Channel channel = SessionFactory.getSession().getChannel(to);
        // 在线/离线
        if (Objects.nonNull(channel)) {
            channel.writeAndFlush(new ChatResponseMessage(from, content));
        } else {
            ctx.writeAndFlush(new ChatResponseMessage(false, "用户不存在或不在线"));
        }
    }
}
