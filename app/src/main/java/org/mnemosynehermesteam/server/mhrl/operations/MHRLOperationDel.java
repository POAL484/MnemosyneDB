package org.mnemosynehermesteam.server.mhrl.operations;

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

public class MHRLOperationDel extends MHRLOperation {

    public MHRLOperationResult handle(ManipObject<Object> manip, MHRLRequestBody mhrlRequestBody) {
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("value", manip.delete()).build());
    }

    @Override
    protected String operationName() { return "DEL"; }

    @Override
    public SematicPipeline makeManipulateWithObject(Core core, UUID userId, String key) {
        return new SematicPipelineBuilder(core, userId, key)
                                    .forAnyClass((manip, req) -> handle(manip, req))
                                    .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null ) return MHRLSyntaxParseResult.generateNullFields("key");
        return MHRLSyntaxParseResult.generateOK();
    }
    
}
