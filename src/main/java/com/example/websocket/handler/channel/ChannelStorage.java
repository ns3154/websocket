package com.example.websocket.handler.channel;

import com.example.websocket.model.Group;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 *      本地仓库
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/02/25 14:14
 **/
public class ChannelStorage {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor
            .INSTANCE);

    public static Map<String, Group> groupMap = new ConcurrentHashMap<>(16);
}
