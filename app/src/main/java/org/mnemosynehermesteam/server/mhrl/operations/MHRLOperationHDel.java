package org.mnemosynehermesteam.server.mhrl.operations;

import java.util.HashMap;
import java.util.List;
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

public class MHRLOperationHDel extends MHRLOperation {

    public MHRLOperationResult handle(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        @SuppressWarnings("unchecked")
        ManipObject<Map<Object, Object>> manip = (ManipObject<Map<Object, Object>>) manipRaw;
        List<String> mapKeys = mhrlRequestBody.mapKeys();
        Map<String, Object> answer = new HashMap<>();
        for ( String key : mapKeys ) {
            answer.put(key, manip.cacheObject.remove(key));
        }
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("deleted", answer).build());
    }

    @Override
    protected String operationName() { return "HDEL"; }

    @Override
    public ManipulateWithObject makeManipulateWithObject(Core core, UUID userId, String key) {
        return new ManipulateWithObjectBuilder(core, userId, key)
                                        .forClass(Map.class, (manip, req) -> handle(manip, req))
                                        .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null || reqData.mapKeys() == null ) return MHRLSyntaxParseResult.generateNullFields("key and mapKeys");
        return MHRLSyntaxParseResult.generateOK();
    }
    
}
