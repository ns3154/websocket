package com.example.websocket.model;

import io.netty.channel.group.ChannelGroup;

/**
 * <pre>
 *
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/02/26 15:34
 **/
public class User {

    private String userId;

    private String groupId;

    private String userName;

    private ChannelGroup userChannels;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ChannelGroup getUserChannels() {
        return userChannels;
    }

    public void setUserChannels(ChannelGroup userChannels) {
        this.userChannels = userChannels;
    }
}
