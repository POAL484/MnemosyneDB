package org.mnemosynehermesteam.disk_storage;

import org.mnemosynehermesteam.core.CacheDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main {
  public static void main(String[] args) {
    DiskStorage diskStorage = new DiskStorage();
    Map<String, String> cacheMap = new HashMap<>();
    cacheMap.put("a", "1");
    cacheMap.put("b", "2");
    diskStorage.saveCache(cacheMap);

    Map<String, String> loadCache = new HashMap<>();
    loadCache = diskStorage.loadCache();
    for (Map.Entry<String, String> entry : loadCache.entrySet()) {
      System.out.println(entry.getKey() + " " + entry.getValue());
    }


    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    long ts = 1000;
    CacheDTO dto = new CacheDTO(id, userId, ts, "ффффффф");

    diskStorage.save("a1", dto);
    diskStorage.save("b1", dto);
    diskStorage.save("c1", dto);


    Map<String, CacheDTO> mapexample = diskStorage.loadCacheDTO();

    for (Map.Entry<String, CacheDTO> entry : mapexample.entrySet()) {
      System.out.println(entry.getKey() + " " + entry.getValue());
    }
  }
}
