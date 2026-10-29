package org.mnemosynehermesteam.server;

import io.netty.handler.codec.http.HttpResponseStatus;

record ErrorObject(int status, String error) {}
record OKRespData(int status, Object data) {}

public record Response(HttpResponseStatus status, Object respData) {
    public static Response generateError(HttpResponseStatus status, String description) {
        return new Response(status, new ErrorObject(status.code(), description));
    }
    public static Response generateOK(Object data) {
        return new Response(HttpResponseStatus.OK, new OKRespData(200, data));
    }
}
