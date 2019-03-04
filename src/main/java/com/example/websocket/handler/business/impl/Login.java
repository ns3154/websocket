package com.example.websocket.handler.business.impl;

import com.example.websocket.handler.channel.ChannelStorage;
import com.example.websocket.annotation.HandType;
import com.example.websocket.constant.RequestType;
import com.example.websocket.handler.business.BusinessHandler;
import com.example.websocket.model.Group;
import com.example.websocket.model.Message;
import com.example.websocket.model.Request;
import com.example.websocket.model.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 *     登录业务处理
 *     此处抽象类 作用 选择性 实现 BusinessHandler 接口
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/03/01 12:32
 **/
@HandType(type = RequestType.LOGIN_TYPE)
public class Login implements BusinessHandler {

    @Override
    public Message<Object> handle(Request request, ChannelHandlerContext ctx) {

        Channel channel = ctx.channel();
        if (StringUtils.isEmpty(request.getUserId()) || StringUtils.isEmpty(request.getGroupId())) {
            channel.writeAndFlush(new TextWebSocketFrame("必要参数丢失"));
            return new Message<>().fail(100, null, "请求参数丢失");
        }
        User user = new User();
        user.setUserId(request.getUserId());
        user.setGroupId(request.getGroupId());
        ChannelGroup channels = user.getUserChannels();
        if (null == channels) {
            channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        }
        channels.add(channel);
        user.setUserChannels(channels);

        Group group = ChannelStorage.groupMap.get(user.getGroupId());

        if (null == group) {
            group = new Group();
            Map<String, User> userMap = group.getUserMap();
            if (null == userMap) {
                userMap = new ConcurrentHashMap<>(14);
            }
            userMap.put(user.getUserId(), user);
            group.setUserMap(userMap);

            ChannelGroup groupChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            groupChannels.add(channel);
            group.setGroupChannels(groupChannels);
            ChannelStorage.groupMap.put(user.getGroupId(), group);

        } else {
            Map<String, User> userMap = group.getUserMap();
            if (null == userMap) {
                userMap = new ConcurrentHashMap<>(14);
            }
            User u = userMap.get(user.getUserId());
            if (null == u) {
                u = user;
                userMap.put(u.getUserId(), u);
            } else {
                u.getUserChannels().add(channel);
            }

            group.getGroupChannels().add(channel);
        }
        ChannelStorage.channels.add(channel);
        return new Message<>().ok(200, null, "登录成功");

    }
}
