package org.mnemosynehermesteam.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class NettyServer implements Runnable {

    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    
    @Override
    public void run() {
        log.info("Starting server at thread");
        try {
            runSync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void runSync() throws InterruptedException {

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new HttpServerCodec());
                    ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
                    ch.pipeline().addLast(new HttpHandler());
                }
            });

            Channel serverChannel = serverBootstrap.bind(5445).sync().channel();

            log.info("Server started on 5445 port!");

            serverChannel.closeFuture().sync();
        } finally {
            close();
        }

    }

    public void close() throws InterruptedException {
        log.info("Closing server...");
        bossGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();
        log.info("Server closed");
    }
}
