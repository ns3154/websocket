package com.example.websocket.model;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/02/26 15:30
 **/
public class Group {

    private Map<String, User> userMap;

    private ChannelGroup groupChannels;

    public Map<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    public ChannelGroup getGroupChannels() {
        return groupChannels;
    }

    public void setGroupChannels(ChannelGroup groupChannels) {
        this.groupChannels = groupChannels;
    }
}
