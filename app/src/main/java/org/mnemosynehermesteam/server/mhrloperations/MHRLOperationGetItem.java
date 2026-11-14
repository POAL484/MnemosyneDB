package org.mnemosynehermesteam.server.mhrloperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public class MHRLOperationGetItem extends MHRLOperation {

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        ManipulateWithObject manip = startObjectManipulation(userId, reqData.key());
        if ( manip.isNull() ) return manip.generateNullError();
        List<Object> items = reqData.item();
        if ( manip.cacheObject instanceof List<?> o ) {
            try {
                if ( items.size() == 1 ) {
                    JsonObject jsonObj = new JsonObject();
                    jsonObj.add("value",
                        DataManipulationUtils.serialiseObject(new Object[]{o.get(((Number) items.get(0)).intValue())})
                    );
                    return MHRLOperationResult.generateOK(jsonObj);
                }
                Object[] respObjects = new Object[items.size()];
                for ( int i = 0; i < items.size(); i++ ) {
                    respObjects[i] = o.get(((Number) items.get(0)).intValue());
                }
                JsonObject jsonObj = new JsonObject();
                jsonObj.add("value", DataManipulationUtils.serialiseObject(respObjects));
                return MHRLOperationResult.generateOK(jsonObj);
            } catch ( IndexOutOfBoundsException e ) {
                return MHRLOperationResult.generateErr(5, "GETITEM out of bounds in list");
            }
        }
        if ( manip.cacheObject instanceof Map<?, ?> o ) {
            Map<Object, Object> respMap = new HashMap<>();
            for ( int i = 0; i < items.size(); i++ ) {
                respMap.put(items.get(i), o.get((Object) items.get(i)));
            }
            JsonObject jsonObj = new JsonObject();
            jsonObj.add("value", DataManipulationUtils.serialiseObject(respMap));
            return MHRLOperationResult.generateOK(jsonObj);
        }
        return generateTypesMismatches();
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        System.out.println(reqData.item());
        if ( reqData.key() != null && reqData.item() != null )
            return reqData.item().size() != 0; // TODO проверка на тип внутри
        return false;
    }

    @Override
    String operationName() { return "GETITEM"; }
    
}
