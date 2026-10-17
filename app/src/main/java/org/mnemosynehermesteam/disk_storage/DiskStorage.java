package org.mnemosynehermesteam.disk_storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiskStorage {
  Path filePath = Path.of("cache_data.csv");
  public void saveCache(Map<String, String> cacheMap) {
    try {
      List<String> lines = new ArrayList<>();
      for (Map.Entry<String, String> entry : cacheMap.entrySet()) {
        lines.add(entry.getKey() + "," + entry.getValue());
      }
      Files.write(filePath, lines);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Map<String, String> loadCache() {
    Map<String, String> cacheMap = new HashMap<>();
    try {
      if (Files.exists(filePath)) {
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
          String[] parts = line.split(",", 2);
          if (parts.length == 2) {
            cacheMap.put(parts[0], parts[1]);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cacheMap;
  }
}