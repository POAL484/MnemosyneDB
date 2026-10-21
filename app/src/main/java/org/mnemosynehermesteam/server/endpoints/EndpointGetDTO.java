package org.mnemosynehermesteam.server.endpoints;

import java.util.Date;
import java.util.UUID;

import org.mnemosynehermesteam.AppCore;
import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.server.Response;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

record GetDTORequestBody(String key) {}

public class EndpointGetDTO extends Endpoint {

    @Override
    public Response handle(FullHttpRequest request) {
        
        GetDTORequestBody reqData;
        try {
            reqData = new Gson().fromJson(request.content().toString(CharsetUtil.UTF_8), GetDTORequestBody.class);
        } catch ( JsonSyntaxException e ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: invalid json");
        }

        if ( checkFieldsIsnotNull(reqData) ) { return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request"); }

        if ( !request.headers().contains("user") ) { return Response.generateError(HttpResponseStatus.UNAUTHORIZED, "'User' header is not provided"); }

        UUID userId;
        try {
            userId = UUID.fromString(request.headers().get("user"));
        } catch ( IllegalArgumentException e ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad user id provided");
        }

        CacheDTO cacheDTO = AppCore.getCore().readCache(userId, reqData.key());

        cacheDTO = new CacheDTO(UUID.randomUUID(), userId, new Date().getTime(), "Tomat!!"); // stub for module testing

        if ( userId.equals(UUID.fromString("95c337df-789e-11ec-ae92-0242ac120002")) ) { // stub for module testing
            cacheDTO = null;
        }

        if ( cacheDTO == null ) {
            return Response.generateError(HttpResponseStatus.NOT_FOUND, "cache with %s key not found".formatted(reqData.key()));
        }

        return Response.generateOK(cacheDTO);
    }
    
}
