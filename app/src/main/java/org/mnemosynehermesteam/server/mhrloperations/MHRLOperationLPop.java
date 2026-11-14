package org.mnemosynehermesteam.server.mhrloperations;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public class MHRLOperationLPop extends MHRLOperation {

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        ManipulateWithObject manip = startObjectManipulation(userId, reqData.key());
        if ( manip.isNull() ) return manip.generateNullError();
        if ( manip.cacheObject instanceof List<?> o ) {
            List<Object> copiedList = new LinkedList<>();
            copiedList.addAll(o);
            if ( copiedList.size() == 0 ) {
                return MHRLOperationResult.generateErr(5, "Out of bounds in list for LPOP");
            }
            Object poped = copiedList.remove(0);
            manip.cacheObject = copiedList;
            manip.saveToCache();
            JsonObject jsonObj = new JsonObject();
            jsonObj.add("value", DataManipulationUtils.serialiseObject(poped));
            return MHRLOperationResult.generateOK(jsonObj);
        }
        return generateTypesMismatches();
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        return reqData.key() != null;
    }

    @Override
    String operationName() { return "LPOP"; }
    
}
