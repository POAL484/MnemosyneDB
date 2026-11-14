package org.mnemosynehermesteam.server.mhrloperations;

import java.util.UUID;

import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public class MHRLOperationDel extends MHRLOperation {

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        CacheDTO cacheDTO = core.deleteCache(reqData.key());
        Object deleted = null;
        if ( cacheDTO != null ) deleted = cacheDTO.cacheObject();
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("value", DataManipulationUtils.serialiseObject(deleted));
        return MHRLOperationResult.generateOK(jsonObj);
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        return reqData.key() != null;
    }

    @Override
    String operationName() { return "DEL"; }
    
}
