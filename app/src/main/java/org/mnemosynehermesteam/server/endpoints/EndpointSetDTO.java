package org.mnemosynehermesteam.server.endpoints;

import java.util.Date;
import java.util.UUID;

import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.DataManipulationUtils;
import org.mnemosynehermesteam.server.Response;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

record SetDTORequestBody(String key, Object val) {}

@Slf4j 
public class EndpointSetDTO extends Endpoint {

    @Override
    public Response handle(FullHttpRequest request) {

        log.debug("Handling [POST|PUT] / with request body: {}", request.content().toString(CharsetUtil.UTF_8)); 

        SetDTORequestBody reqData = DataManipulationUtils.parseGson(request.content().toString(CharsetUtil.UTF_8), SetDTORequestBody.class);

        if ( reqData == null ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: JSON syntax error");
        }
        if ( reqData.key() == null ) {
            log.debug("Set failed: key is null"); 
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: key is null or not passed");
        }
        if ( reqData.val() == null ) {
            log.debug("Set failed: val is null"); 
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: val is null, use DELETE to remove");
        }

        UUID userId = DataManipulationUtils.authorizate(request.headers());

        if ( userId == null ) {
            return Response.generateError(HttpResponseStatus.UNAUTHORIZED, "Unauthorized: header 'user' must be uuid4");
        }

        if ( !(reqData.val() instanceof Long) && !(reqData.val() instanceof Double) && !(reqData.val() instanceof String) && !(reqData.val() instanceof Boolean) ) {
            log.debug("Setting cache with key {} failed: unsupported type in val {}", reqData.key(), reqData.val());
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: unsupported cacheObject type");
        }

        CacheDTO cacheDTO = new CacheDTO(userId, UUID.randomUUID(), new Date().getTime(), reqData.val());

        Core.getCore().saveCache(cacheDTO, reqData.key());

        log.debug("Successfully handled set cache with key: {} and val: {}", reqData.key(), reqData.val());

        return Response.generateOK(cacheDTO);

    }
    
}
