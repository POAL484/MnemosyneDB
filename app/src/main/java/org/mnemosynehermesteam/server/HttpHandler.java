package org.mnemosynehermesteam.server;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.mnemosynehermesteam.server.endpoints.Endpoint;
import org.mnemosynehermesteam.server.endpoints.EndpointDelDTO;
import org.mnemosynehermesteam.server.endpoints.EndpointGetDTO;
import org.mnemosynehermesteam.server.endpoints.EndpointSetDTO;

import com.google.gson.Gson;

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

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    final Map<EndpointEntry, Endpoint> ENDPOINTS = new HashMap<>();

    HttpHandler() {
        ENDPOINTS.put(new EndpointEntry("/", HttpMethod.GET), new EndpointGetDTO());
        ENDPOINTS.put(new EndpointEntry("/", HttpMethod.POST), new EndpointSetDTO());
        ENDPOINTS.put(new EndpointEntry("/", HttpMethod.PUT), new EndpointSetDTO());
        ENDPOINTS.put(new EndpointEntry("/", HttpMethod.DELETE), new EndpointDelDTO());
    }

    private FullHttpResponse createResponseText(HttpResponseStatus status, String bodyText, HttpHeaders headers) {
        headers.set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(bodyText, StandardCharsets.UTF_8), headers, new DefaultHttpHeaders());
    }

    private FullHttpResponse createResponseJson(HttpResponseStatus status, Object obj, HttpHeaders headers) {
        headers.set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(new Gson().toJson(obj), StandardCharsets.UTF_8), headers, new DefaultHttpHeaders());
    }

    private void sendRespToChannel(ChannelHandlerContext ctx, FullHttpResponse resp) {
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {

        EndpointEntry endpointEntry = new EndpointEntry(req.uri(), req.method());

        if ( ! ENDPOINTS.containsKey(endpointEntry) ) {
            sendRespToChannel(ctx, createResponseJson(HttpResponseStatus.NOT_FOUND, Response.generateError(HttpResponseStatus.NOT_FOUND, "Endpoint not found"), new DefaultHttpHeaders()));
        }
        
        Response resp;
        try {
            resp = ENDPOINTS.get(endpointEntry).handle(req);
        } catch ( Exception e ) {
            e.printStackTrace();
            sendRespToChannel(ctx, createResponseJson(HttpResponseStatus.INTERNAL_SERVER_ERROR, Response.generateError(HttpResponseStatus.INTERNAL_SERVER_ERROR, "Internal server error("), new DefaultHttpHeaders()));
            return;
        }

        if ( resp.respData() instanceof String ) {
            sendRespToChannel(ctx, createResponseText(resp.status(), (String) resp.respData(), new DefaultHttpHeaders()));
        }
        else {
            sendRespToChannel(ctx, createResponseJson(resp.status(), resp.respData(), new DefaultHttpHeaders()));
        }

    }
    
}
