package org.mnemosynehermesteam.server.mhrloperations;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public class MHRLOperationIsMember extends MHRLOperation {

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        ManipulateWithObject manip = startObjectManipulation(userId, reqData.key());
        if ( manip.isNull() ) return manip.generateNullError();
        if ( manip.cacheObject instanceof Set<?> o ) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.add("isMember", DataManipulationUtils.serialiseObject(o.contains(reqData.value())));
            return MHRLOperationResult.generateOK(jsonObj);            
        }
        if ( manip.cacheObject instanceof Map<?, ?> o ) {
            JsonObject jsonObj = new JsonObject();
            jsonObj.add("isMember", DataManipulationUtils.serialiseObject(o.containsKey(reqData.value())));
            return MHRLOperationResult.generateOK(jsonObj);
        }
        return generateTypesMismatches();
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        return reqData.key() != null && reqData.value() != null;
    }

    @Override
    String operationName() { return "ISMEMBER"; }
    
}
