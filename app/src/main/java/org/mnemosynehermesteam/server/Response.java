package org.mnemosynehermesteam.server;

import io.netty.handler.codec.http.HttpResponseStatus;

record ErrorObject(int status, String err) {}
record ErrorWithCodeObject(int status, String err, int errCode) {}
record OKRespData(int status, Object data) {}

public record Response(HttpResponseStatus status, Object respData) {
    public static Response generateError(HttpResponseStatus status, String description) {
        return new Response(status, new ErrorWithCodeObject(status.code(), description, 1));
    }
    public static Response generateOK(Object data) {
        return new Response(HttpResponseStatus.OK, new OKRespData(200, data));
    }
    public static Response generateBadRequestWithCode(String description, int errCode) {
        return new Response(HttpResponseStatus.BAD_REQUEST, new ErrorWithCodeObject(400, description, errCode));
    }
}
