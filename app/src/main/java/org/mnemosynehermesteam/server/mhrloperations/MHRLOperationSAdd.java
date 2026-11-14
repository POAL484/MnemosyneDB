package org.mnemosynehermesteam.server.mhrloperations;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public class MHRLOperationSAdd extends MHRLOperation {

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        ManipulateWithObject manip = startObjectManipulation(userId, reqData.key());
        if ( manip.isNull() ) manip.createNew(new HashSet<>());
        if ( manip.cacheObject instanceof Set<?> o ) {
            Set<Object> copiedSet = new HashSet<>();
            copiedSet.addAll(o);
            System.out.println(copiedSet);
            copiedSet.add(reqData.value());
            manip.cacheObject = copiedSet;
            manip.saveToCache();
            JsonObject jsonObj = new JsonObject();
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
    String operationName() { return "SADD"; }
    
}
