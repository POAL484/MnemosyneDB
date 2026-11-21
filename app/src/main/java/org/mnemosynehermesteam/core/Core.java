package org.mnemosynehermesteam.core;

import org.mnemosynehermesteam.caching.InMemoryStorage;
import org.mnemosynehermesteam.config.MnemosyneConfig;
import org.mnemosynehermesteam.diskstorage.DiskStorage;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.time.Duration;

@Slf4j 
public class Core {

    static final private Core core = new Core();

    private final InMemoryStorage memory;
    private final DiskStorage disk;
    private final boolean diskEnabled;

    private Core(){
        MnemosyneConfig config = new MnemosyneConfig();
        this.memory = new InMemoryStorage(config.getAlgorithm());
        this.diskEnabled = config.isDiskStorageEnabled();
        if (diskEnabled){
            this.disk = new DiskStorage();
            loadFromDisk();
        }
        else{
            this.disk = null;
        }
    }

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

        if (!diskEnabled) {
            return null;
        }

        CacheDTO fromDisk = disk.loadCacheDTO().get(new FullKey(userId, key));
        if (fromDisk != null){
            memory.save(key, fromDisk);
        }
        log.debug("Loaded cache {} from disk: {}", key, fromDisk);
        return fromDisk;
    }

    public void saveCache(CacheDTO cacheDTO, String key) {
        log.debug("Save cache {} with val {} to memory and disk", key, cacheDTO.cacheObject());
        memory.save(key, cacheDTO);
        if (diskEnabled) {
            disk.save(key, cacheDTO);
        }
    }

    public void saveCache(CacheDTO cacheDTO, String key, Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("TTL must be positive");
        }

        if (diskEnabled) {
            throw new UnsupportedOperationException(
                    "TTL with disk storage is not implemented yet"
            );
        }

        memory.save(key, cacheDTO, ttl);
            disk.save(new FullKey(cacheDTO.userId(), key), cacheDTO);
        }
    }

    public CacheDTO deleteCache(String key) {
        CacheDTO removed = memory.get(key).orElse(null);
        memory.delete(key);
        log.warn("Cache object deletion not implemented yet");
        return removed;
    }

    private void loadFromDisk() {
        Map<FullKey, CacheDTO> fromDisk = disk.loadCacheDTO();

        List<Map.Entry<FullKey, CacheDTO>> entries = new ArrayList<>(fromDisk.entrySet());
        entries.sort(Comparator.comparingLong(entry -> entry.getValue().ts()));

        for (Map.Entry<FullKey, CacheDTO> entry : entries) {
            memory.save(entry.getKey().key(), entry.getValue());
        }
    }

}
