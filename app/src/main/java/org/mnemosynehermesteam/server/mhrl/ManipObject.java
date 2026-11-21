package org.mnemosynehermesteam.server.mhrl;

import java.util.Date;
import java.util.UUID;

import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.core.Core;
import org.mnemosynehermesteam.server.DataManipulationUtils;

import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class ManipObject <T> {

    public T cacheObject = null;
    private CacheDTO originalCacheDto;
    private final String key;
    private final Core core;
    private final UUID userId;

    public ManipObject(Core core, CacheDTO cacheDTO, String key, UUID userId) {
        this.core = core;
        this.originalCacheDto = cacheDTO;
        this.key = key;
        this.userId = userId;
        if ( cacheDTO != null ) {
            cacheObject = (T) cacheDTO.cacheObject();
        }
        log.debug("Started manipulate with {} key, {} cacheObject", key, cacheObject);
    }

    public JsonElement serialized() {
        return DataManipulationUtils.serialiseObject(cacheObject);
    }

    public void create(T object) {
        originalCacheDto = new CacheDTO(UUID.randomUUID(), userId, new Date().getTime(), object);
        core.saveCache(originalCacheDto, key);
        cacheObject = (T) originalCacheDto.cacheObject();
    }

    public void replace(T object) {
        if ( originalCacheDto == null ) {
            create(object);
            return;
        }
        originalCacheDto = new CacheDTO(originalCacheDto.id(), originalCacheDto.userId(), new Date().getTime(), object);
        core.saveCache(originalCacheDto, key);
        cacheObject = (T) originalCacheDto.cacheObject();
    }

    public Object delete() {
        CacheDTO cacheDTO = core.deleteCache(key);
        if ( cacheDTO != null ) return cacheDTO.cacheObject();
        return null;
    }

}
