package org.mnemosynehermesteam.server.mhrl.operations;

import java.util.Collection;
import java.util.Map;
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

public class MHRLOperationSize extends MHRLOperation {

    MHRLOperationResult handleCollection(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        ManipObject<Collection<Object>> manip = (ManipObject<Collection<Object>>) manipRaw;
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("size", manip.cacheObject.size()).build());
    }

    MHRLOperationResult handleMap(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        ManipObject<Map<Object, Object>> manip = (ManipObject<Map<Object, Object>>) manipRaw;
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("size", manip.cacheObject.size()).build());
    }

    MHRLOperationResult handleString(ManipObject<String> manip, MHRLRequestBody mhrlRequestBody) {
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("size", manip.cacheObject.length()).build());
    }

    @Override
    protected String operationName() { return "SIZE"; }

    @Override
    public ManipulateWithObject makeManipulateWithObject(Core core, UUID userId, String key) {
        return new ManipulateWithObjectBuilder(core, userId, key)
                                            .forClass(Collection.class, (manip, req) -> handleCollection(manip, req))
                                            .forClass(Map.class, (manip, req) -> handleMap(manip, req))
                                            .forClass(String.class, (manip, req) -> handleString(manip, req))
                                            .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null ) return MHRLSyntaxParseResult.generateNullFields("key");
        return MHRLSyntaxParseResult.generateOK();
    }
    
}
