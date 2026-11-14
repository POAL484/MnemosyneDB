package org.mnemosynehermesteam.server.mhrloperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public class MHRLOperationHDel extends MHRLOperation {

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        ManipulateWithObject manip = startObjectManipulation(userId, reqData.key());
        if ( manip.isNull() ) return manip.generateNullError();
        List<String> mapKeys = reqData.mapKeys();
        if ( manip.cacheObject instanceof Map<?, ?> o ) {
            Map<Object, Object> copiedMap = new HashMap<>();
            copiedMap.putAll(o);
            Map<Object, Object> removed = new HashMap<>();
            for ( int i = 0; i < mapKeys.size(); i++ ) {
                removed.put(mapKeys.get(i), copiedMap.remove(mapKeys.get(i)));
            }
            manip.cacheObject = copiedMap;
            manip.saveToCache();
            JsonObject jsonObj = new JsonObject();
            jsonObj.add("deleted", DataManipulationUtils.serialiseObject(removed));
            return MHRLOperationResult.generateOK(jsonObj);
        }
        return generateTypesMismatches();
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        return reqData.key() != null && reqData.mapKeys() != null;
    }

    @Override
    String operationName() { return "HDEL"; }
    
}
