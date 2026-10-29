package org.mnemosynehermesteam.disk_storage;

import java.util.HashMap;
import java.util.Map;

public class Main {
  public static void main(String[] args) {
    DiskStorage diskStorage = new DiskStorage();
    Map<String, String> cacheMap = new HashMap<>();
    cacheMap.put("1", "2");
    cacheMap.put("3", "4");
    diskStorage.saveCache(cacheMap);

    Map<String, String> loadCache = new HashMap<>();
    loadCache = diskStorage.loadCache();
    for (Map.Entry<String, String> entry : loadCache.entrySet()) {
      System.out.println(entry.getKey() + " " + entry.getValue());
    }
  }
}
