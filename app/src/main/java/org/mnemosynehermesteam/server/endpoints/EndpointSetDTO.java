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

record SetDTORequestBody(String key, Object val) {}

public class EndpointSetDTO extends Endpoint {

    @Override
    public Response handle(FullHttpRequest request) {
        
        SetDTORequestBody reqData;
        try {
            reqData = new Gson().fromJson(request.content().toString(CharsetUtil.UTF_8), SetDTORequestBody.class);
        } catch ( JsonSyntaxException e ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request");
        }

        if ( checkFieldsIsnotNull(reqData) ) { return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request"); }

        if ( !request.headers().contains("user") ) { return Response.generateError(HttpResponseStatus.UNAUTHORIZED, "'User' header is not provided"); }

        UUID userId;
        try {
            userId = UUID.fromString(request.headers().get("user"));
        } catch ( IllegalArgumentException e ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad user id provided");
        }

        CacheDTO cacheDTO = new CacheDTO(userId, UUID.randomUUID(), new Date().getTime(), reqData.val());

        System.out.printf("Writed %s to storage\n", cacheDTO); // stub for module testing

        AppCore.getCore().saveCache(cacheDTO, reqData.key());

        return Response.generateOK(cacheDTO);

    }
    
}
