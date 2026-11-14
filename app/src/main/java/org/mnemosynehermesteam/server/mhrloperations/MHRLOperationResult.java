package org.mnemosynehermesteam.server.mhrloperations;

import com.google.gson.JsonObject;

public record MHRLOperationResult(boolean status, int code, JsonObject jsonObj, String errDescription) {
    public static MHRLOperationResult generateOK(JsonObject jsonObj) {
        return new MHRLOperationResult(true, 0, jsonObj, null);
    }
    public static MHRLOperationResult generateErr(int code, String errDescription) {
        return new MHRLOperationResult(false, code, null, errDescription);
    }
}
