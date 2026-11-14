package org.mnemosynehermesteam.server.endpoints;

import java.util.UUID;

import org.mnemosynehermesteam.server.DataManipulationUtils;
import org.mnemosynehermesteam.server.Response;
import org.mnemosynehermesteam.server.mhrloperations.MHRLOperation;
import org.mnemosynehermesteam.server.mhrloperations.MHRLOperationResult;
import org.mnemosynehermesteam.server.mhrloperations.MHRLRequestBody;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class EndpointMHRL extends Endpoint {
    
    @Override
    public Response handle(FullHttpRequest request) {
        UUID userId = DataManipulationUtils.authorizate(request.headers());

        if ( userId == null ) {
            return Response.generateError(HttpResponseStatus.UNAUTHORIZED, "Unauthorized: header 'user' must be uuid4");
        }

        MHRLRequestBody reqData = DataManipulationUtils.parseGson(request.content().toString(CharsetUtil.UTF_8), MHRLRequestBody.class);

        if ( reqData == null ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request: JSON syntax error");
        }

        if ( !MHRLOperation.containsOperation(reqData.op()) ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Unknown operation: %s".formatted(reqData.op()));
        }

        MHRLOperation opHandler = MHRLOperation.getOperation(reqData.op());

        if ( !opHandler.checkRequest(reqData) ) {
            return Response.generateError(HttpResponseStatus.BAD_REQUEST, "Bad request for operation %s".formatted(reqData.op()));
        }

        MHRLOperationResult res = opHandler.handle(userId, reqData);

        if ( !res.status() ) {
            return Response.generateErrorWithCode(res.errDescription(), res.code());
        }

        return Response.generateOK(res.jsonObj());
    }
    
}
