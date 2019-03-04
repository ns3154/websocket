package com.example.websocket.utils;

import com.example.websocket.handler.channel.ChannelStorage;
import com.example.websocket.model.Group;
import com.example.websocket.model.User;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * <pre>
 *
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/02/27 09:17
 **/
public class SendUtil {

    public static void send(String groupId, String userId, String msg) {
        if (!StringUtils.isEmpty(groupId) && !StringUtils.isEmpty(userId)) {
            sendToUser(groupId, userId, msg);
        } else if (!StringUtils.isEmpty(groupId) && StringUtils.isEmpty(userId)) {
            sendToGroup(groupId, msg);
        } else if (StringUtils.isEmpty(groupId) && !StringUtils.isEmpty(userId)) {
            sendToUser(userId, msg);
        } else {
            sendAll(msg);
        }
    }

    private static void sendToUser(String userId, String msg) {
        Map<String, Group> groupMap = ChannelStorage.groupMap;
        for (Map.Entry<String, Group> g : groupMap.entrySet()) {
            Map<String, User> userMap = g.getValue().getUserMap();

            for (Map.Entry<String, User> u : userMap.entrySet()) {
                if (u.getKey().equals(userId)) {
                    u.getValue().getUserChannels().writeAndFlush(new TextWebSocketFrame(msg));
                    break;
                }
            }
        }
    }

    private static void sendToUser(String groupId, String userId, String msg) {
        Group group = ChannelStorage.groupMap.get(groupId);
        if (null != group) {
            Map<String, User> userMap = group.getUserMap();
            if (null != userMap && !userMap.isEmpty()) {
                userMap.get(userId).getUserChannels().writeAndFlush(new TextWebSocketFrame(msg));
            }
        }
    }

    private static void sendToGroup(String groupId, String msg) {
        Map<String, Group> groupMap = ChannelStorage.groupMap;
        if (null != groupId && !groupMap.isEmpty()) {
            Group group = groupMap.get(groupId);
            if (null != group) {
                group.getGroupChannels().writeAndFlush(new TextWebSocketFrame(msg));
            }
        }
    }

    private static void sendAll(String msg) {
        ChannelStorage.channels.writeAndFlush(new TextWebSocketFrame(msg));
    }


}
