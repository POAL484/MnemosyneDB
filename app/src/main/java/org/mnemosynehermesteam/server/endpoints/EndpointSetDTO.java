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

record SetDTORequestBody(String key, Object val) {}

public class EndpointSetDTO extends Endpoint {

    @Override
    public Response handle(FullHttpRequest request) {

        SetDTORequestBody reqData = DataManipulationUtils.parseGson(request.content().toString(CharsetUtil.UTF_8), SetDTORequestBody.class);

        if ( reqData == null ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: JSON syntax error");
        }
        if ( reqData.key() == null ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: key is null or not passed");
        }
        if ( reqData.val() == null ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: val is null, use DELETE to remove");
        }

        UUID userId = DataManipulationUtils.authorizate(request.headers());

        if ( userId == null ) {
            return Response.generateError(HttpResponseStatus.UNAUTHORIZED, "Unauthorized: header 'user' must be uuid4");
        }

        CacheDTO cacheDTO = new CacheDTO(userId, UUID.randomUUID(), new Date().getTime(), reqData.val());

        System.out.printf("Writed %s to storage\n", cacheDTO); // stub for module testing

        Core.getCore().saveCache(cacheDTO, reqData.key());

        return Response.generateOK(cacheDTO);

    }
    
}
