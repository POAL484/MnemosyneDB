package org.mnemosynehermesteam.server.mhrl;

import java.util.UUID;

import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonObject;

public abstract class MHRLOperation {

    abstract public ManipulateWithObject makeManipulateWithObject(Core core, UUID userId, String key);

    abstract public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData);

    abstract protected String operationName();

    MHRLOperationResult generateSimpleOK() {
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("OK", DataManipulationUtils.serialiseObject("OK!"));
        return MHRLOperationResult.generateOK(jsonObj);
    }

    MHRLOperationResult generateTypesMismatches() {
        return MHRLOperationResult.generateErr(3, "types mismatches for %s".formatted(operationName()));
    }
}
