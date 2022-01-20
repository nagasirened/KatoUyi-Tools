package com.katouyi.tools.netty.im.handler;

import com.katouyi.tools.netty.im.group.GroupSessionFactory;
import com.katouyi.tools.netty.im.message.GroupChatRequestMessage;
import com.katouyi.tools.netty.im.message.GroupChatResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String content = msg.getContent();
        if (StringUtils.isEmpty(content)) {
            ctx.writeAndFlush(new GroupChatResponseMessage(false, "内容为空"));
        }

        String from = msg.getFrom();
        if (StringUtils.isEmpty(from)) {
            ctx.writeAndFlush(new GroupChatResponseMessage(false, "账号异常"));
        }

        String groupName = msg.getGroupName();
        if (StringUtils.isBlank(groupName)) {
            ctx.writeAndFlush(new GroupChatResponseMessage(false, "聊天组已解散"));
        }
        List<Channel> channelList = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
        for (Channel channel : channelList) {
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
    }
}
