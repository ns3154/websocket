package com.example.websocket.handler.channel;

import com.alibaba.fastjson.JSON;
import com.example.websocket.handler.business.BusinessHandler;
import com.example.websocket.handler.business.HandlerContext;
import com.example.websocket.utils.CloseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.websocket.model.*;

/**
 * <pre>
 *      通道处理
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/02/25 14:12
 **/
public class FtChannelHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger log = LoggerFactory.getLogger(FtChannelHandler.class);

    private static final String URI = "ws";

    private static final String WEBSOCKET = "websocket";

    private static final String UPGRADE = "Upgrade";

    private WebSocketServerHandshaker handshaker;

    private static final int OK = 200;


    /**
     * 断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.info("【handlerRemoved:断开连接】====> {}", ctx.channel().id());
        CloseUtil.close(ctx);
    }

    /**
     * 连接异常   需要关闭相关资源
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("【系统异常】======> {}", cause.toString());
        ctx.close();
        ctx.channel().close();
    }

    /**
     * 不活跃的通道  就说明用户失去连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("【channelInactive】=====> {}", ctx.channel());
    }

    /**
     * 这里只要完成 flush
     * channel 通道 Read 读取 Complete 完成
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 这里是保持服务器与客户端长连接  进行心跳检测 避免连接断开
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent stateEvent = (IdleStateEvent) evt;
            switch (stateEvent.state()) {
                case READER_IDLE:
                    log.info("【{}】读空闲（服务器端）", ctx.channel().remoteAddress());
                    break;
                case WRITER_IDLE:
                    log.info("【{}】写空闲（客户端）", ctx.channel().remoteAddress());
                    break;
                case ALL_IDLE:
                    log.info("【{}】读写空闲", ctx.channel().remoteAddress());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 收发消息处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, WebSocketFrame msg) {
        if (msg instanceof HttpRequest) {
            doHandlerHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            doHandlerWebSocketFrame(ctx, msg);
        }
    }

    /**
     * websocket消息处理
     * @param ctx
     * @param msg
     */
    private void doHandlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame msg) {
        //判断msg 是哪一种类型  分别做出不同的反应
        if (msg instanceof CloseWebSocketFrame) {
            log.info("【关闭】");
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) msg);
            return;
        }
        if (msg instanceof PingWebSocketFrame) {
            log.info("【ping】");
            PongWebSocketFrame pong = new PongWebSocketFrame(msg.content().retain());
            ctx.channel().writeAndFlush(pong);
            return;
        }
        if (msg instanceof PongWebSocketFrame) {
            log.info("【pong】");
            PingWebSocketFrame ping = new PingWebSocketFrame(msg.content().retain());
            ctx.channel().writeAndFlush(ping);
            return;
        }
        if (!(msg instanceof TextWebSocketFrame)) {
            log.info("【不支持二进制】");
            throw new UnsupportedOperationException("不支持二进制");
        }

        String req = ((TextWebSocketFrame) msg).text();

        Request request = JSON.parseObject(req, Request.class);

        BusinessHandler bhd = HandlerContext.getHandlerContext(request.getRequestType() + "");
        bhd.handle(request, ctx);
    }


    /**
     * wetsocket第一次连接握手
     * @param ctx
     * @param msg
     */
    private void doHandlerHttpRequest(ChannelHandlerContext ctx, HttpRequest msg) {
        // http 解码失败
        if (!msg.getDecoderResult().isSuccess() || (!WEBSOCKET.equals(msg.headers().get(UPGRADE)))) {
            sendHttpResponse(ctx, (FullHttpRequest) msg, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
        }
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://" + msg.headers().get("Host") + "/" + URI + "", null, false);
        handshaker = factory.newHandshaker(msg);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
            return;
        }
        //进行连接
        handshaker.handshake(ctx.channel(), (FullHttpRequest) msg);
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.getStatus().code() != OK) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != OK) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
