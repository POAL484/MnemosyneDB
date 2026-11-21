package org.mnemosynehermesteam.server.mhrl.operations;

import java.util.Set;
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

public class MHRLOperationSRem extends MHRLOperation {

    MHRLOperationResult handle(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        ManipObject<Set<Object>> manip = (ManipObject<Set<Object>>) manipRaw;
        boolean contains = manip.cacheObject.remove(mhrlRequestBody.value());
        return MHRLOperationResult.generateOK(new JsonObjectBuilder()
                                        .add("size", manip.cacheObject.size())
                                        .add("value", contains ? mhrlRequestBody.value() : null)
                                        .build());
    }

    @Override
    protected String operationName() { return "SREM"; }

    @Override
    public ManipulateWithObject makeManipulateWithObject(Core core, UUID userId, String key) {
        return new ManipulateWithObjectBuilder(core, userId, key)
                                                .forClass(Set.class, (manip, req) -> handle(manip, req))
                                                .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null ) return MHRLSyntaxParseResult.generateNullFields("key");
        return MHRLSyntaxParseResult.generateOK();
    }
    
}
