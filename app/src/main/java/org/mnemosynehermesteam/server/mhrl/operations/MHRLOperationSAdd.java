package org.mnemosynehermesteam.server.mhrl.operations;

import java.util.HashSet;
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

public class MHRLOperationSAdd extends MHRLOperation {

    MHRLOperationResult handle(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        ManipObject<Set<Object>> manip = (ManipObject<Set<Object>>) manipRaw;
        manip.cacheObject.add(mhrlRequestBody.value());
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("size", manip.cacheObject.size()).build());
    }

    @Override
    protected String operationName() { return "SADD"; }

    @Override
    public SematicPipeline makeManipulateWithObject(Core core, UUID userId, String key) {
        return new SematicPipelineBuilder(core, userId, key)
                                                .addDefault(new HashSet<>())
                                                .forClass(Set.class, (manip, req) -> handle(manip, req))
                                                .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null || reqData.value() == null ) return MHRLSyntaxParseResult.generateNullFields("key and value");
        return MHRLSyntaxParseResult.generateOK();
    }
    
}
