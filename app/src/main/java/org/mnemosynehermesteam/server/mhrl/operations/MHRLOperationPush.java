package org.mnemosynehermesteam.server.mhrl.operations;

import java.util.LinkedList;
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

enum OperationPush {
    LPUSH("LPUSH"), RPUSH("RPUSH");

    public final String opName;

    OperationPush(String opName) {
        this.opName = opName;
    }
}

public class MHRLOperationPush extends MHRLOperation {

    private final OperationPush operation;
    
    MHRLOperationPush(OperationPush operation) {
        this.operation = operation;
    }

    static public MHRLOperationPush createLPush() {
        return new MHRLOperationPush(OperationPush.LPUSH);
    }

    static public MHRLOperationPush createRPush() {
        return new MHRLOperationPush(OperationPush.RPUSH);
    }

    public MHRLOperationResult handle(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        ManipObject<List<Object>> manip = (ManipObject<List<Object>>) manipRaw;
        switch (operation) {
            case LPUSH:
                manip.cacheObject.add(0, mhrlRequestBody.value());
                break;
            case RPUSH:
                manip.cacheObject.add(mhrlRequestBody.value());
                break;
        }
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("size", manip.cacheObject.size()).build());
    }

    @Override
    protected String operationName() { return operation.opName; }

    @Override
    public SematicPipeline makeManipulateWithObject(Core core, UUID userId, String key) {
        return new SematicPipelineBuilder(core, userId, key)
                                            .addDefault(new LinkedList<>())
                                            .forClass(List.class, (manip, req) -> handle(manip, req))
                                            .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null || reqData.value() == null ) return MHRLSyntaxParseResult.generateNullFields("key and value");
        return MHRLSyntaxParseResult.generateOK();
    }
    
}
