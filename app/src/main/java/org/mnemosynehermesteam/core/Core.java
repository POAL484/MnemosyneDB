package org.mnemosynehermesteam.core;

import org.mnemosynehermesteam.caching.InMemoryStorage;
import org.mnemosynehermesteam.config.MnemosyneConfig;
import org.mnemosynehermesteam.diskstorage.DiskStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
        Optional<CacheDTO> fromMemory = memory.get(key);

        if (fromMemory.isPresent()){
            return fromMemory.get();
        }

        if (!diskEnabled) {
            return null;
        }

        CacheDTO fromDisk = disk.loadCacheDTO().get(key);
        if (fromDisk != null){
            memory.save(key, fromDisk);
        }
        return fromDisk;
    }

    public void saveCache(CacheDTO cacheDTO, String key) {
        memory.save(key, cacheDTO);
        if (diskEnabled) {
            disk.save(key, cacheDTO);
        }
    }

    public CacheDTO deleteCache(String key) {
        CacheDTO removed = memory.get(key).orElse(null);
        memory.delete(key);
        return removed;
    }

    private void loadFromDisk() {
        Map<String, CacheDTO> fromDisk = disk.loadCacheDTO();

        List<Map.Entry<String, CacheDTO>> entries = new ArrayList<>(fromDisk.entrySet());
        entries.sort(Comparator.comparingLong(entry -> entry.getValue().ts()));

        for (Map.Entry<String, CacheDTO> entry : entries) {
            memory.save(entry.getKey(), entry.getValue());
        }
    }

}
