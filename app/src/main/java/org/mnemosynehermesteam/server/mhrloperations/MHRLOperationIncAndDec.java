package org.mnemosynehermesteam.server.mhrloperations;

import java.util.UUID;

import com.google.gson.JsonObject;

enum Operation {
    INC, DEC
}

public class MHRLOperationIncAndDec extends MHRLOperation {

    private final Operation operation;
    
    MHRLOperationIncAndDec(Operation operation) {
        this.operation = operation;
    }

    static public MHRLOperationIncAndDec createInc() {
        return new MHRLOperationIncAndDec(Operation.INC);
    }

    static public MHRLOperationIncAndDec createDec() {
        return new MHRLOperationIncAndDec(Operation.INC);
    }

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        Number opNumber = (Number) reqData.value();
        ManipulateWithObject manip = startObjectManipulation(userId, reqData.key());
        if ( manip.isNull() ) return manip.generateNullError();
        if ( manip.cacheObject instanceof Double o ) {
            JsonObject jsonObj = new JsonObject();
            switch (operation) {
                case INC:
                    manip.cacheObject = o + opNumber.doubleValue();
                    manip.saveToCache();
                    jsonObj.add("value", manip.serialized());
                    break;
                case DEC:
                    manip.cacheObject = o - opNumber.doubleValue();
                    manip.saveToCache();
                    jsonObj.add("value", manip.serialized());
                    break;
            }
            return MHRLOperationResult.generateOK(jsonObj);
        }
        if ( manip.cacheObject instanceof Long o ) {
            JsonObject jsonObj = new JsonObject();
            switch (operation) {
                case INC:
                    manip.cacheObject = o + opNumber.longValue();
                    manip.saveToCache();
                    jsonObj.add("value", manip.serialized());
                    break;
                case DEC:
                    manip.cacheObject = o - opNumber.longValue();
                    manip.saveToCache();
                    jsonObj.add("value", manip.serialized());
                    break;
            }
            return MHRLOperationResult.generateOK(jsonObj);
        }
        return generateTypesMismatches();
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        if ( reqData.value() instanceof Number ) return reqData.key() != null;
        return false;
    }

    @Override
    String operationName() { return "INC|DEC"; }
    
}
