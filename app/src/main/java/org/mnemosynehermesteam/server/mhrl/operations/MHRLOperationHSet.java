package org.mnemosynehermesteam.server.mhrl.operations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.mhrl.JsonObjectBuilder;
import org.mnemosynehermesteam.server.mhrl.MHRLOperation;
import org.mnemosynehermesteam.server.mhrl.MHRLOperationResult;
import org.mnemosynehermesteam.server.mhrl.MHRLRequestBody;
import org.mnemosynehermesteam.server.mhrl.MHRLSyntaxParseResult;
import org.mnemosynehermesteam.server.mhrl.ManipObject;
import org.mnemosynehermesteam.server.mhrl.SematicPipeline;
import org.mnemosynehermesteam.server.mhrl.SematicPipelineBuilder;

public class MHRLOperationHSet extends MHRLOperation {

    public MHRLOperationResult handle(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        ManipObject<Map<Object, Object>> manip = (ManipObject<Map<Object, Object>>) manipRaw;
        Map<Object, Object> toUpdate = (Map<Object, Object>) mhrlRequestBody.value();
        manip.cacheObject.putAll(toUpdate);
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("size", manip.cacheObject.size()).build());
    }

    @Override
    protected String operationName() { return "HSET"; }

    @Override
    public SematicPipeline makeManipulateWithObject(Core core, UUID userId, String key) {
        return new SematicPipelineBuilder(core, userId, key)
                                            .addDefault(new HashMap<>())
                                            .forClass(Map.class, (manip, req) -> handle(manip, req))
                                            .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null || reqData.value() == null ) return MHRLSyntaxParseResult.generateNullFields("key and value");
        if ( reqData.value() instanceof Map<?, ?> ) return MHRLSyntaxParseResult.generateOK();
        return MHRLSyntaxParseResult.generateJsonSyntaxError();
    }
    
}
