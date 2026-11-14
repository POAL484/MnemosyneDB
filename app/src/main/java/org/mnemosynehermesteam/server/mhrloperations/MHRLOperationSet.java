package org.mnemosynehermesteam.server.mhrloperations;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.mnemosynehermesteam.core.CacheDTO;

public class MHRLOperationSet extends MHRLOperation {

    @Override
    public MHRLOperationResult handle(UUID userId, MHRLRequestBody reqData) {
        if ( reqData.value() instanceof Map<?, ?> o ) {
            if ( o.get("$set") instanceof List<?> listForSet ) {
                Set<Object> set = new HashSet<>(listForSet);
                core.saveCache(
                    new CacheDTO(UUID.randomUUID(), userId, new Date().getTime(), set),
                    reqData.key()
                );
                return generateSimpleOK();
            }
        }
        core.saveCache(
            new CacheDTO(UUID.randomUUID(), userId, new Date().getTime(), reqData.value()),
            reqData.key()
        );
        return generateSimpleOK();
    }

    @Override
    public boolean checkRequest(MHRLRequestBody reqData) {
        return reqData.key() != null && reqData.value() != null;
    }

    @Override
    String operationName() { return "SET"; }
    
}
