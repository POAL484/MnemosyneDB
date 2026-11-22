package org.mnemosynehermesteam.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mnemosynehermesteam.core.CacheDTO;
import org.mnemosynehermesteam.core.Core;

public class CoreStub extends Core {
    CoreStub() {}

    private final Map<String, CacheDTO> dbMap = new HashMap<>();
    
    @Override 
    public CacheDTO readCache(UUID userId, String key) {
        return dbMap.get(key);
    }

    @Override 
    public void saveCache(CacheDTO cacheDTO, String key) {
        dbMap.put(key, cacheDTO);
    }

    @Override 
    public CacheDTO deleteCache(String key) {
        return dbMap.remove(key);
    }

}
