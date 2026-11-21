package org.mnemosynehermesteam.server.endpoints;

import java.util.UUID;

import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.DataManipulationUtils;
import org.mnemosynehermesteam.server.Response;
import org.mnemosynehermesteam.server.mhrl.MHRL;
import org.mnemosynehermesteam.server.mhrl.MHRLOperationResult;
import org.mnemosynehermesteam.server.mhrl.MHRLRequestBody;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class EndpointMHRL extends Endpoint {
    
    private final MHRL mhrl;

    public EndpointMHRL(Core core) {
        super(core);
        this.mhrl = new MHRL(core);
    }

    @Override
    public Response handle(FullHttpRequest request) {
        UUID userId = DataManipulationUtils.authorizate(request.headers());

        if ( userId == null ) {
            return Response.generateError(HttpResponseStatus.UNAUTHORIZED, "Unauthorized: header 'user' must be uuid4");
        }

        MHRLRequestBody reqData = DataManipulationUtils.parseGson(request.content().toString(CharsetUtil.UTF_8), MHRLRequestBody.class);

        if ( reqData == null ) {
            return Response.generateBadRequestWithCode("Json syntax error or incorrect fields types", -1);
        }

        if ( !mhrl.containsOperation(reqData.op()) ) {
            return Response.generateBadRequestWithCode("Unknown operation: %s".formatted(reqData.op()), -2);
        }

        MHRLOperationResult res = mhrl.makeMHRLRequest(reqData.op(), userId, reqData);

        if ( !res.status() ) {
            return Response.generateBadRequestWithCode(res.errDescription(), res.code());
        }

        return Response.generateOK(res.jsonObj());
    }
    
}
