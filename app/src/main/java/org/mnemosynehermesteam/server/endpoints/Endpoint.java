package org.mnemosynehermesteam.server.endpoints;

import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.Response;

import io.netty.handler.codec.http.FullHttpRequest;

public abstract class Endpoint {

    private final Core core;

    public Endpoint(Core core) {
        this.core = core;
    }
    
    public abstract Response handle(FullHttpRequest request);
}
