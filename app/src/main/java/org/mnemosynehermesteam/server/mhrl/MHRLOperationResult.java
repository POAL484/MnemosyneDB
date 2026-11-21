package org.mnemosynehermesteam.server.mhrl;

import com.google.gson.JsonObject;

public record MHRLOperationResult(boolean status, int code, JsonObject jsonObj, String errDescription) {
    public static MHRLOperationResult generateOK(JsonObject jsonObj) {
        return new MHRLOperationResult(true, 0, jsonObj, null);
    }
    public static MHRLOperationResult generateErr(int code, String errDescription) {
        return new MHRLOperationResult(false, code, null, errDescription);
    }
    public static MHRLOperationResult generateNPE(String opName, String key) {
        return generateErr(2, "%s value on key %s is null".formatted(opName, key));
    }
    public static MHRLOperationResult generateTypesMismatch(String opName, String key) {
        return generateErr(1, "types mismatches for %s and key %s".formatted(opName, key));
    }
}
