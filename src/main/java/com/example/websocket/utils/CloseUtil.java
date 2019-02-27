package com.example.websocket.utils;

import com.example.websocket.ChannelStorage;
import com.example.websocket.model.Group;
import com.example.websocket.model.User;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * <pre>
 *
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/02/27 17:48
 **/
public class CloseUtil {

    public static void close(ChannelHandlerContext ctx) {
         String channelLongId = ctx.channel().id().asLongText();

        ChannelStorage.channels.remove(ctx);

        Map<String, Group> groupMap = ChannelStorage.groupMap;

    }

    private static void groupClose(ChannelHandlerContext ctx, Map<String, Group> groupMap, String channelLongId) {
        for (Map.Entry<String, Group> gm : groupMap.entrySet()) {
            Group g = gm.getValue();
            g.getGroupChannels().remove(ctx);
            userClose(ctx, g.getUserMap());

        }
    }

    private static void userClose(ChannelHandlerContext ctx, Map<String, User> userMap) {
        if(null != userMap && !userMap.isEmpty()) {
            for (Map.Entry<String, User> um : userMap.entrySet()) {
                User value = um.getValue();
                if(null != value) {
                    value.getUserChannels().remove(ctx);
                }
            }
        }
    }



}
