package org.mnemosynehermesteam.server.mhrloperations;

import java.util.Date;
import java.util.UUID;

import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonElement;

public class ManipulateWithObject {
    
    public Object cacheObject = null;
    private CacheDTO originalCacheDto;
    private final String key;
    private final String operationName;
    private final Core core;
    private final UUID userId;

    public ManipulateWithObject(Core core, UUID userId, String key, String operationName) {
        this.core = core;
        this.key = key;
        this.operationName = operationName;
        this.userId = userId;
        originalCacheDto = core.readCache(userId, key);
        if ( originalCacheDto != null ) {
            cacheObject = originalCacheDto.cacheObject();
        }
    }

    public boolean isNull() {
        return cacheObject == null;
    }

    public void createNew(Object object) {
        cacheObject = object;
        originalCacheDto = new CacheDTO(UUID.randomUUID(), userId, new Date().getTime(), object);
    }

    public MHRLOperationResult generateNullError() {
        return MHRLOperationResult.generateErr(4, "%s value on key %s is null".formatted(operationName, key));
    }

    public JsonElement serialized() {
        return DataManipulationUtils.serialiseObject(cacheObject);
    }

    public void saveToCache() {
        core.saveCache(
            new CacheDTO(originalCacheDto.id(), originalCacheDto.userId(), new Date().getTime(), cacheObject),
            key
        );
    }
}
