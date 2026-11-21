package org.mnemosynehermesteam.server.mhrl.operations;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.mhrl.JsonObjectBuilder;
import org.mnemosynehermesteam.server.mhrl.MHRLOperation;
import org.mnemosynehermesteam.server.mhrl.MHRLOperationResult;
import org.mnemosynehermesteam.server.mhrl.MHRLRequestBody;
import org.mnemosynehermesteam.server.mhrl.MHRLSyntaxParseResult;
import org.mnemosynehermesteam.server.mhrl.ManipObject;
import org.mnemosynehermesteam.server.mhrl.ManipulateWithObject;
import org.mnemosynehermesteam.server.mhrl.ManipulateWithObjectBuilder;

public class MHRLOperationSet extends MHRLOperation {

    MHRLOperationResult handle(ManipObject<Object> manip, MHRLRequestBody mhrlRequestBody) {
        Object value = mhrlRequestBody.value();
        if ( value instanceof Map<?, ?> map ) {
            if ( map.containsKey("$set") ) {
                if ( map.get("$set") instanceof List<?> elements ) {
                    value = new HashSet<>();
                    ((Set<Object>)value).addAll(elements);
                }
            }
        }
        manip.replace(value);
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("OK", "OK!").build());
    }

    @Override
    protected String operationName() { return "SET"; }

    @Override
    public ManipulateWithObject makeManipulateWithObject(Core core, UUID userId, String key) {
        return new ManipulateWithObjectBuilder(core, userId, key)
                                                .forAnyClass((manip, req) -> handle(manip, req))
                                                .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null || reqData.value() == null ) return MHRLSyntaxParseResult.generateNullFields("key and value");
        return MHRLSyntaxParseResult.generateOK();
    }
    
}
