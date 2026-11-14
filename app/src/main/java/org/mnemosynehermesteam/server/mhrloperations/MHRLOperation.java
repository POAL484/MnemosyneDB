package org.mnemosynehermesteam.server.mhrloperations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public abstract class MHRLOperation {

    Core core = Core.getCore();

    static Map<String, MHRLOperation> OPS = new HashMap<>();

    static {
        OPS.put("GET", new MHRLOperationGet());
        OPS.put("GETITEM", new MHRLOperationGetItem());
        OPS.put("ISMEMBER", new MHRLOperationIsMember());
        OPS.put("SIZE", new MHRLOperationSize());
        OPS.put("SET", new MHRLOperationSet());
        OPS.put("INC", MHRLOperationIncAndDec.createInc());
        OPS.put("DEC", MHRLOperationIncAndDec.createDec());
        OPS.put("LPUSH", new MHRLOperationLPush());
        OPS.put("RPUSH", new MHRLOperationRPush());
        OPS.put("LPOP", new MHRLOperationLPop());
        OPS.put("RPOP", new MHRLOperationRPop());
        OPS.put("SADD", new MHRLOperationSAdd());
        OPS.put("SREM", new MHRLOperationSRem());
        OPS.put("DEL", new MHRLOperationDel());
        OPS.put("HSET", new MHRLOperationHSet());
        OPS.put("HDEL", new MHRLOperationHDel());
    }

    static public boolean containsOperation(String op) {
        return OPS.containsKey(op);
    }
    
    static public MHRLOperation getOperation(String op) {
        return OPS.get(op);
    }

    abstract public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData);

    abstract public boolean checkRequest(MHRLRequestBody reqData);

    abstract String operationName();

    ManipulateWithObject startObjectManipulation(UUID userID, String key) {
        return new ManipulateWithObject(core, userID, key, operationName());
    }

    MHRLOperationResult generateSimpleOK() {
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("OK", DataManipulationUtils.serialiseObject("OK!"));
        return MHRLOperationResult.generateOK(jsonObj);
    }

    MHRLOperationResult generateTypesMismatches() {
        return MHRLOperationResult.generateErr(3, "types mismatches for %s".formatted(operationName()));
    }
}
