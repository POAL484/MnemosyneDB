package org.mnemosynehermesteam.server.endpoints;

import org.mnemosynehermesteam.server.Response;

import io.netty.handler.codec.http.FullHttpRequest;

public abstract class Endpoint {
    
    public abstract Response handle(FullHttpRequest request);
}
