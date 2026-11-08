package org.mnemosynehermesteam.server.endpoints;

import java.util.UUID;

import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.DataManipulationUtils;
import org.mnemosynehermesteam.server.Response;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

record DelDTORequestBody(String key) {}

@Slf4j 
public class EndpointDelDTO extends Endpoint {

    @Override
    public Response handle(FullHttpRequest request) {

        log.debug("Handling [DELETE] / with request body: {}", request.content().toString(CharsetUtil.UTF_8)); 

        DelDTORequestBody reqData = DataManipulationUtils.parseGson(request.content().toString(CharsetUtil.UTF_8), DelDTORequestBody.class);

        if ( reqData == null ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: JSON syntax error");
        }
        if ( reqData.key() == null ) {
            log.debug("Del failed: key is null"); 
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: key is null or not passed");
        }

        UUID userId = DataManipulationUtils.authorizate(request.headers());

        if ( userId == null ) {
            return Response.generateError(HttpResponseStatus.UNAUTHORIZED, "Unauthorized: header 'user' must be uuid4");
        }

        CacheDTO cacheDTO = Core.getCore().readCache(userId, reqData.key());

        if ( cacheDTO == null ) {
            log.debug("Delete with key {} failed: not found", reqData.key());
            return Response.generateError(HttpResponseStatus.NOT_FOUND, "cache with %s key not found".formatted(reqData.key()));
        }

        Core.getCore().deleteCache(reqData.key());

        log.debug("Successfully handled deleting cache with key: {}", reqData.key());

        return Response.generateOK(cacheDTO);
    }
    
}
