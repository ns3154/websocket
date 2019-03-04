package com.example.websocket.controller;

import com.alibaba.fastjson.JSON;
import com.example.websocket.handler.channel.ChannelStorage;
import com.example.websocket.model.Group;
import com.example.websocket.utils.SendUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.channels.Channels;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/02/26 11:06
 **/
@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping("showList")
    public String showList() {
        Map<String, Group> groupMap = ChannelStorage.groupMap;
        Map<String, Object> map = new HashMap<>();
        map.put("groupMap", groupMap);
        map.put("size", ChannelStorage.channels.size());
        return JSON.toJSONString(map);
    }

    @PostMapping("sendClientsMsg")
    public String sendClientMsg(String userId, String groupId, String msg) {
        SendUtil.send(groupId, userId, msg);
        return "ok";
    }


}
