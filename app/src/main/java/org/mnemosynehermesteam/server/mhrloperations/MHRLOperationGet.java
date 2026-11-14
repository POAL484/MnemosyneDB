package org.mnemosynehermesteam.server.mhrloperations;

import java.util.UUID;

import com.google.gson.JsonObject;

public class MHRLOperationGet extends MHRLOperation {

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        ManipulateWithObject manip = startObjectManipulation(userId, reqData.key());
        JsonObject obj = new JsonObject();
        obj.add("value", manip.serialized());
        return MHRLOperationResult.generateOK(obj);
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        return reqData.key() != null;
    }

    @Override
    String operationName() { return "GET"; }
    
}
