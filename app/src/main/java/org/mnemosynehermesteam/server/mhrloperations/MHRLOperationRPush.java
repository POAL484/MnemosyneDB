package org.mnemosynehermesteam.server.mhrloperations;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public class MHRLOperationRPush extends MHRLOperation {

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        ManipulateWithObject manip = startObjectManipulation(userId, reqData.key());
        if ( manip.isNull() ) manip.createNew(new LinkedList<>());
        if ( manip.cacheObject instanceof List<?> o ) {
            List<Object> copiedList = new LinkedList<>();
            copiedList.addAll(o);
            copiedList.add(reqData.value());
            manip.cacheObject = copiedList;
            manip.saveToCache();
            JsonObject jsonObj = new JsonObject();
            jsonObj.add("size", DataManipulationUtils.serialiseObject(copiedList.size()));
            return MHRLOperationResult.generateOK(jsonObj);
        }
        return generateTypesMismatches();
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        return reqData.key() != null && reqData.value() != null;
    }

    @Override
    String operationName() { return "RPUSH"; }
    
}
