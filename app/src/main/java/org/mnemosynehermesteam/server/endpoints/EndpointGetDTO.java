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

record GetDTORequestBody(String key) {}

@Slf4j 
public class EndpointGetDTO extends Endpoint {

    @Override
    public Response handle(FullHttpRequest request) {
        
        log.debug("Handling [GET] / with request body: {}", request.content().toString(CharsetUtil.UTF_8)); // TODO перенести в HttpHandler
        
        GetDTORequestBody reqData = DataManipulationUtils.parseGson(request.content().toString(CharsetUtil.UTF_8), GetDTORequestBody.class);

        if ( reqData == null ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: JSON syntax error");
        }
        if ( reqData.key() == null ) {
            log.debug("Get failed: key is null"); 
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: key is null or not passed");
        }

        UUID userId = DataManipulationUtils.authorizate(request.headers());

        if ( userId == null ) {
            return Response.generateError(HttpResponseStatus.UNAUTHORIZED, "Unauthorized: header 'user' must be uuid4");
        }

        CacheDTO cacheDTO = Core.getCore().readCache(userId, reqData.key());

        if ( cacheDTO == null ) {
            log.debug("Getting with key {} failed: not found", reqData.key());
            return Response.generateError(HttpResponseStatus.NOT_FOUND, "cache with %s key not found".formatted(reqData.key()));
        }
        
        log.debug("Successfully handled get cache with key: {}", reqData.key());

        return Response.generateOK(cacheDTO);
    }
    
}
