package org.mnemosynehermesteam.server.mhrl.operations;

import java.util.UUID;

import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.mhrl.JsonObjectBuilder;
import org.mnemosynehermesteam.server.mhrl.MHRLOperation;
import org.mnemosynehermesteam.server.mhrl.MHRLOperationResult;
import org.mnemosynehermesteam.server.mhrl.MHRLRequestBody;
import org.mnemosynehermesteam.server.mhrl.MHRLSyntaxParseResult;
import org.mnemosynehermesteam.server.mhrl.ManipObject;
import org.mnemosynehermesteam.server.mhrl.ManipulateWithObject;
import org.mnemosynehermesteam.server.mhrl.ManipulateWithObjectBuilder;

public class MHRLOperationGet extends MHRLOperation {

    MHRLOperationResult handle(ManipObject<Object> manip, MHRLRequestBody req) {
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("value", manip.cacheObject).build());
    }

    @Override
    protected String operationName() { return "GET"; }

    @Override
    public ManipulateWithObject makeManipulateWithObject(Core core, UUID userId, String key) {
        return new ManipulateWithObjectBuilder(core, userId, key)
                                        .forAnyClass((manip, req) -> handle(manip, req))
                                        .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null ) return MHRLSyntaxParseResult.generateNullFields("key");
        return MHRLSyntaxParseResult.generateOK();
    }
    
}
