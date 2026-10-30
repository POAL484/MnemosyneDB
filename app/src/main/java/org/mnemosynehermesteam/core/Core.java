package org.mnemosynehermesteam.core;

import java.util.UUID;

public class Core {

    static final private Core core = new Core();

    public static Core getCore() {
        return core;
    }

    public CacheDTO readCache(UUID userId, String key) {
        return null;
    }

    public void saveCache(CacheDTO cacheDTO, String key) {
        
    }

    public CacheDTO deleteCache(String key) {
        return null;
    }

}
