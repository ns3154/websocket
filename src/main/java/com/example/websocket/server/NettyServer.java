package com.example.websocket.server;

import com.example.websocket.handler.channel.FtChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *     netty
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/02/25 14:05
 **/
@Component
public class NettyServer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${netty.server.port}")
    private int port;
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    @PostConstruct()
    public void init(){
        /**
         * 需要开启一个新的线程来执行netty server 服务器
         */
        new Thread(() -> startServer()).start();
    }

    private void startServer(){
        //服务端需要2个线程组  boss处理客户端连接  work进行客服端连接之后的处理
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            //服务器 配置
            bootstrap.group(boss,work).channel(NioServerSocketChannel.class)
                     .option(ChannelOption.SO_BACKLOG, 1024)
                     .localAddress(port)
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // HttpServerCodec：将请求和应答消息解码为HTTP消息
                            socketChannel.pipeline().addLast("http-codec", new HttpServerCodec());
                            // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
                            socketChannel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                            // ChunkedWriteHandler：向客户端发送HTML5文件
                            socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            // 进行设置心跳检测
                            socketChannel.pipeline().addLast(new IdleStateHandler(60,30,60*30, TimeUnit.SECONDS));
                            // 配置通道处理  来进行业务处理
                            socketChannel.pipeline().addLast(new FtChannelHandler());
                            socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("ws"));
                        }
                    }).option(ChannelOption.SO_BACKLOG,1024).childOption(ChannelOption.SO_KEEPALIVE,true);
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
            bootstrap.bind().sync().channel().closeFuture().sync();
            logger.info("websocket启动");
        }catch (Exception e){
            logger.error(" NETTYSERVER START ERROR -->{}", e);
        }finally {
            //关闭资源
            boss.shutdownGracefully();
            work.shutdownGracefully();
            logger.warn("NETTYSERVER CLOSE");
        }
    }

}
