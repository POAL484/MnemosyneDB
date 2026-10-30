package org.mnemosynehermesteam.disk_storage;

import org.mnemosynehermesteam.core.CacheDTO;

import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DiskStorage {
  Path filePath = Path.of("userdata.csv");
  public void save(String key, CacheDTO cacheDTO) {
    try {
      UUID id = cacheDTO.id();
      UUID userId = cacheDTO.userId();
      long ts = cacheDTO.ts();
      Object cacheObject = cacheDTO.cacheObject();
      String line = key + "," + id + "," + userId + "," + ts + "," + cacheObject + "\n";
      Files.writeString(filePath, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public Map<String, CacheDTO> loadCacheDTO() {
    Path filePath = Path.of("userdata.csv");
    Map<String, CacheDTO> cacheMap = new HashMap<>();
    try {
      if (Files.exists(filePath)) {
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
          String[] parts = line.split(",", 5);
          if (parts.length == 5) {
            String key = parts[0];
            UUID id = UUID.fromString(parts[1]);
            UUID userId = UUID.fromString(parts[2]);
            long ts = Long.parseLong(parts[3]);
            Object cacheObject = parts[4];
            cacheMap.put(key, new CacheDTO(id, userId, ts, cacheObject));
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cacheMap;
  }
}