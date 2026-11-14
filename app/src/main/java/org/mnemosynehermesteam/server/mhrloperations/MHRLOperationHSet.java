package org.mnemosynehermesteam.server.mhrloperations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public class MHRLOperationHSet extends MHRLOperation {

    @SuppressWarnings("unchecked")
    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        ManipulateWithObject manip = startObjectManipulation(userId, reqData.key());
        if ( manip.isNull() ) manip.createNew(new HashMap<>());
        if ( manip.cacheObject instanceof Map<?, ?> o ) {
            Map<Object, Object> copiedMap = new HashMap<>();
            copiedMap.putAll(o);
            copiedMap.putAll((Map<Object, Object>) reqData.value());
            manip.cacheObject = copiedMap;
            manip.saveToCache();
            JsonObject jsonObj = new JsonObject();
            jsonObj.add("size", DataManipulationUtils.serialiseObject(copiedMap.size()));
            return MHRLOperationResult.generateOK(jsonObj);
        }
        return generateTypesMismatches();
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        if ( reqData.value() instanceof Map<?, ?> ) return reqData.key() != null;
        return false;
    }

    @Override
    String operationName() { return "HSET"; }
    
}
