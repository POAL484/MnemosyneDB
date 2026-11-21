package org.mnemosynehermesteam.server.mhrl.operations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class MHRLOperationGetItem extends MHRLOperation {

    MHRLOperationResult handleList(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        @SuppressWarnings("unchecked")
        ManipObject<List<Object>> manip = (ManipObject<List<Object>>) manipRaw;
        List<Object> answer = new LinkedList<>();
        List<Object> idxs = mhrlRequestBody.item();
        try {
            for ( int i = 0; i < idxs.size(); i++ ) {
                answer.add(manip.cacheObject.get(((Number) idxs.get(i)).intValue()));
            }
        } catch ( IndexOutOfBoundsException e ) {
            return MHRLOperationResult.generateErr(4, "GETITEM out of bounds in list");
        } catch ( ClassCastException e ) {
            return MHRLOperationResult.generateErr(-1, "GETITEM item indexes must be int to get from list");
        }
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("value", answer).build());
    }


    MHRLOperationResult handleMap(ManipObject<?> manipRaw, MHRLRequestBody mhrlRequestBody) {
        @SuppressWarnings("unchecked")
        ManipObject<Map<Object, Object>> manip = (ManipObject<Map<Object, Object>>) manipRaw;
        Map<Object, Object> answer = new HashMap<>();
        for ( Object key : mhrlRequestBody.item() ) {
            answer.put(key, manip.cacheObject.get(key));
        }
        return MHRLOperationResult.generateOK(new JsonObjectBuilder().add("value", answer).build());
    }

    @Override
    protected String operationName() { return "GETITEM"; }

    @Override
    public ManipulateWithObject makeManipulateWithObject(Core core, UUID userId, String key) {
        return new ManipulateWithObjectBuilder(core, userId, key)
                                            .forClass(List.class, (manip, req) -> handleList(manip, req))
                                            .forClass(Map.class, (manip, req) -> handleMap(manip, req))
                                            .build();
    }

    @Override
    public MHRLSyntaxParseResult syntaxParse(MHRLRequestBody reqData) {
        if ( reqData.key() == null || reqData.item() == null ) return MHRLSyntaxParseResult.generateNullFields("key and item");
        if ( reqData.item().size() == 0 ) return MHRLSyntaxParseResult.generateErr(-3, "GETITEM item argument empty");
        log.debug("Correct syntax for GETITEM: key={} item={}", reqData.key(), reqData.item());
        return MHRLSyntaxParseResult.generateOK();
    }
    
}
