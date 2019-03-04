package com.example.websocket.utils;

import com.example.websocket.handler.channel.ChannelStorage;
import com.example.websocket.model.Group;
import com.example.websocket.model.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.ChannelMatchers;

import java.util.Iterator;
import java.util.Map;

/**
 * <pre>
 *      关闭类
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/02/27 17:48
 **/
public class CloseUtil {

    private CloseUtil() {

    }

    private static final int ZERO = 0;

    public static void close(ChannelHandlerContext ctx) {
        groupClose(ctx, ChannelStorage.groupMap);
        ChannelStorage.channels.remove(ctx);
    }

    private static void groupClose(ChannelHandlerContext ctx, Map<String, Group> groupMap) {

        Iterator<Map.Entry<String, Group>> iterator = groupMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Group> entry = iterator.next();
            int i = userClose(ctx, entry.getValue().getUserMap());

            if (i == ZERO) {
                iterator.remove();
            }
        }

    }

    private static int userClose(ChannelHandlerContext ctx, Map<String, User> userMap) {
        if (null == userMap || userMap.size() == ZERO) {
            return ZERO;
        }

        Iterator<Map.Entry<String, User>> iterator = userMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, User> entry = iterator.next();
            User user = entry.getValue();
            if (null == user) {
                iterator.remove();
            } else {
                int i = channelGroupClose(user.getUserChannels(), ctx);

                if (i == ZERO) {
                    iterator.remove();
                }
            }

        }
        return userMap.size();
    }

    private static int channelGroupClose(ChannelGroup cg, ChannelHandlerContext ctx) {
        if (null == cg || cg.size() == ZERO) {
            return 0;
        }
        cg.remove(ctx);
        return cg.size();
    }

}
