package org.mnemosynehermesteam.core;

import org.mnemosynehermesteam.caching.InMemoryStorage;
import org.mnemosynehermesteam.diskstorage.DiskStorage;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Slf4j 
public class Core {

    static final private Core core = new Core();

    private final InMemoryStorage memory = new InMemoryStorage(); // по умолчанию LRU
    private final DiskStorage disk = new DiskStorage();

    public static Core getCore() {
        return core;
    }

    public CacheDTO readCache(UUID userId, String key) {
        log.debug("Started reading cache {}", key);
        Optional<CacheDTO> fromMemory = memory.get(key);

        if (fromMemory.isPresent()){
            log.debug("Cache {} found in memory, val: {}", key, fromMemory.get().cacheObject());
            return fromMemory.get();
        }

        
        log.debug("Cache {} not found in memory, loading from disk", key);
        CacheDTO fromDisk = disk.loadCacheDTO().get(key);
        if (fromDisk != null){
            memory.save(key, fromDisk);
        }
        log.debug("Loaded cache {} from disk: {}", key, fromDisk.cacheObject());
        return fromDisk;
    }

    public void saveCache(CacheDTO cacheDTO, String key) {
        log.debug("Save cache {} with val {} to memory and disk", key, cacheDTO.cacheObject());
        memory.save(key, cacheDTO);
        disk.save(key, cacheDTO);
    }

    public CacheDTO deleteCache(String key) {
        CacheDTO removed = memory.get(key).orElse(null);
        memory.delete(key);
        log.warn("Cache object deletion not implemented yet");
        return removed;
    }

}
