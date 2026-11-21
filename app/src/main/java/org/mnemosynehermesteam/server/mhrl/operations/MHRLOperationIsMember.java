package org.mnemosynehermesteam.server.mhrl.operations;

import java.util.Map;
import java.util.Set;
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

public class MHRLOperationIsMember extends MHRLOperation {

    public MHRLOperationResult handleSet(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        ManipObject<Set<Object>> manip = (ManipObject<Set<Object>>) manipRaw;
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("isMember", manip.cacheObject.contains(mhrlRequestBody.value())).build());
    }

    public MHRLOperationResult handleMap(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        ManipObject<Map<Object, Object>> manip = (ManipObject<Map<Object, Object>>) manipRaw;
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("isMember", manip.cacheObject.containsKey(mhrlRequestBody.value())).build());
    }

    @Override
    protected String operationName() { return "ISMEMBER"; }

    @Override
    public SematicPipeline makeManipulateWithObject(Core core, UUID userId, String key) {
        return new SematicPipelineBuilder(core, userId, key)
                                        .onNull(manip -> MHRLOperationResult.generateOK(new JsonObjectBuilder().add("isMember", false).build()))
                                        .forClass(Set.class, (manip, req) -> handleSet(manip, req))
                                        .forClass(Map.class, (manip, req) -> handleMap(manip, req))
                                        .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null || reqData.value() == null ) return MHRLSyntaxParseResult.generateNullFields("key and value");
        return MHRLSyntaxParseResult.generateOK();
    }
    
}
