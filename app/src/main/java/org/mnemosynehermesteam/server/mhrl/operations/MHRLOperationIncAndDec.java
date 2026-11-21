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

enum OperationIncDec {
    INC("INC"), DEC("DEC");

    public final String opName;

    OperationIncDec(String opName) {
        this.opName = opName;
    }
}

public class MHRLOperationIncAndDec extends MHRLOperation {

    private final OperationIncDec operation;
    
    MHRLOperationIncAndDec(OperationIncDec operation) {
        this.operation = operation;
    }

    static public MHRLOperationIncAndDec createInc() {
        return new MHRLOperationIncAndDec(OperationIncDec.INC);
    }

    static public MHRLOperationIncAndDec createDec() {
        return new MHRLOperationIncAndDec(OperationIncDec.DEC);
    }

    public MHRLOperationResult handleLong(ManipObject<Long> manip, MHRLRequestBody mhrlRequestBody) {
        try { 
            Long newVal = Long.valueOf(manip.cacheObject.longValue() + 
                        (operation.equals(OperationIncDec.INC) ? ((Number) mhrlRequestBody.value()).longValue() : -((Number) mhrlRequestBody.value()).longValue()));
            manip.replace(newVal);
        } catch ( ClassCastException e ) {
            return MHRLOperationResult.generateErr(-1, "%s value must be number castable".formatted(operationName()));
        }
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("value", manip.cacheObject).build());
    }

    public MHRLOperationResult handleDouble(ManipObject<Double> manip, MHRLRequestBody mhrlRequestBody) {
        try { 
            Double newVal = Double.valueOf(manip.cacheObject.doubleValue() + 
                        (operation.equals(OperationIncDec.INC) ? ((Number) mhrlRequestBody.value()).doubleValue() : -((Number) mhrlRequestBody.value()).doubleValue()));
            manip.replace(newVal);
        } catch ( ClassCastException e ) {
            return MHRLOperationResult.generateErr(-1, "%s value must be number castable".formatted(operationName()));
        }
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("value", manip.cacheObject).build());
    }

    @Override
    protected String operationName() { return operation.opName; }

    @Override
    public ManipulateWithObject makeManipulateWithObject(Core core, UUID userId, String key) {
        return new ManipulateWithObjectBuilder(core, userId, key)
                                            .addDefault(Long.valueOf(0))
                                            .forClass(Long.class, (manip, req) -> handleLong(manip, req))
                                            .forClass(Double.class, (manip, req) -> handleDouble(manip, req))
                                            .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null || reqData.value() == null ) return MHRLSyntaxParseResult.generateNullFields("key and value");
        if ( reqData.value() instanceof Number ) return MHRLSyntaxParseResult.generateOK();
        return MHRLSyntaxParseResult.generateJsonSyntaxError();
    }
    
}
