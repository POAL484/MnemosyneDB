package org.mnemosynehermesteam.server.mhrl.operations;

import java.util.List;
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

enum OperationPop {
    LPOP("LPOP"), RPOP("RPOP");

    public final String opName;

    OperationPop(String opName) {
        this.opName = opName;
    }
}

public class MHRLOperationPop extends MHRLOperation {

    private final OperationPop operation;
    
    MHRLOperationPop(OperationPop operation) {
        this.operation = operation;
    }

    static public MHRLOperationPop createLPop() {
        return new MHRLOperationPop(OperationPop.LPOP);
    }

    static public MHRLOperationPop createRPop() {
        return new MHRLOperationPop(OperationPop.RPOP);
    }

    public MHRLOperationResult handle(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        ManipObject<List<Object>> manip = (ManipObject<List<Object>>) manipRaw;
        if ( manip.cacheObject.size() == 0 ) {
            return MHRLOperationResult.generateErr(3, "%s from empty list".formatted(operationName()));
        }
        Object popped = null;
        switch (operation) {
            case LPOP:
                popped = manip.cacheObject.remove(0);
                break;
            case RPOP:
                popped = manip.cacheObject.remove(manip.cacheObject.size()-1);
                break;
        }
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("value", popped).build());
    }

    @Override
    protected String operationName() { return operation.opName; }

    @Override
    public SematicPipeline makeManipulateWithObject(Core core, UUID userId, String key) {
        return new SematicPipelineBuilder(core, userId, key)
                                            .forClass(List.class, (manip, req) -> handle(manip, req))
                                            .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null ) return MHRLSyntaxParseResult.generateNullFields("key");
        return MHRLSyntaxParseResult.generateOK();
    }
    
}
