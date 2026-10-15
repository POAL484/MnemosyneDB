package org.mnemosynehermesteam.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.nio.charset.StandardCharsets;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private FullHttpResponse createResponseText(HttpResponseStatus status, String bodyText, HttpHeaders headers) {
        headers.set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(bodyText, StandardCharsets.UTF_8), headers, new DefaultHttpHeaders());
    }

    private void sendRespToChannel(ChannelHandlerContext ctx, FullHttpResponse resp) {
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {

        if ( !req.uri().equals("/") ) {
            sendRespToChannel(ctx, createResponseText(HttpResponseStatus.NOT_FOUND, "Not Found!", new DefaultHttpHeaders()));
        }
        if ( !req.method().equals(HttpMethod.GET)) {
            sendRespToChannel(ctx, createResponseText(HttpResponseStatus.METHOD_NOT_ALLOWED, "Method Not Allowed!", new DefaultHttpHeaders()));
        }
        
        sendRespToChannel(ctx, createResponseText(HttpResponseStatus.OK, "Hi Hello Hi!!!!", new DefaultHttpHeaders()));

    }
    
}
