package org.mnemosynehermesteam.server.mhrloperations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public class MHRLOperationSize extends MHRLOperation {

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        ManipulateWithObject manip = startObjectManipulation(userId, reqData.key());
        if ( manip.isNull() ) return manip.generateNullError();
        JsonObject jsonObj = new JsonObject();
        if ( manip.cacheObject instanceof List<?> o ) {
            jsonObj.add("size", DataManipulationUtils.serialiseObject(o.size()));
        }
        else if ( manip.cacheObject instanceof Map<?, ?> o ) {
            jsonObj.add("size", DataManipulationUtils.serialiseObject(o.size()));
        }
        else if ( manip.cacheObject instanceof Set<?> o ) {
            jsonObj.add("size", DataManipulationUtils.serialiseObject(o.size()));
        }
        else if ( manip.cacheObject instanceof String o ) {
            jsonObj.add("size", DataManipulationUtils.serialiseObject(o.length()));
        }
        else {
            return generateTypesMismatches();
        }
        return MHRLOperationResult.generateOK(jsonObj);
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        return reqData.key() != null;
    }

    @Override
    String operationName() { return "SIZE"; }
    
}
