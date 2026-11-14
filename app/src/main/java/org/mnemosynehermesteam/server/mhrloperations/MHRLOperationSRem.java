package org.mnemosynehermesteam.server.mhrloperations;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public class MHRLOperationSRem extends MHRLOperation {

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        ManipulateWithObject manip = startObjectManipulation(userId, reqData.key());
        if ( manip.isNull() ) return manip.generateNullError();
        System.out.println(manip.cacheObject.getClass());
        if ( manip.cacheObject instanceof Set<?> o ) {
            Set<Object> copiedSet = new HashSet<>();
            copiedSet.addAll(o);
            Object popped = copiedSet.remove(reqData.value()) ? reqData.value() : null;
            manip.cacheObject = copiedSet;
            manip.saveToCache();
            JsonObject jsonObj = new JsonObject();
            jsonObj.add("value", DataManipulationUtils.serialiseObject(popped));
            jsonObj.add("size", DataManipulationUtils.serialiseObject(copiedSet.size()));
            return MHRLOperationResult.generateOK(jsonObj);
        }
        return generateTypesMismatches();
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        return reqData.key() != null && reqData.value() != null;
    }

    @Override
    String operationName() { return "SREM"; }
    
}
