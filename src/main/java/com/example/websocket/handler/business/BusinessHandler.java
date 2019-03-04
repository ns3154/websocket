package com.example.websocket.handler.business;

import com.example.websocket.handler.business.impl.Login;
import com.example.websocket.model.Message;
import com.example.websocket.model.Request;
import io.netty.channel.ChannelHandlerContext;

/**
 * <pre>
 *  业务处理逻辑
 *  在收到 messageReceived 消息后 进行业务处理
 *  每增加一个业务处理类型 实现该接口即可,详细请看 {@link Login}
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/03/01 12:29
 **/
public interface BusinessHandler {

    /**
     * 业务处理
     * @param request
     * @param ctx
     * @return
     */
    Message<Object> handle(Request request, ChannelHandlerContext ctx);
}
