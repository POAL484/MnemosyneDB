package org.mnemosynehermesteam.diskstorage;

import org.mnemosynehermesteam.core.CacheDTO;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j 
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
      log.debug("Cache {} with val {} appended to disk storage", key, cacheObject);
    } catch (IOException e) {
      log.error("Disk storage save caused IOException", e);
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
            UUID id;
            try { id = UUID.fromString(parts[1]); }
            catch ( IllegalArgumentException e ) { log.warn("Invalid id: \"{}\", skipping", parts[1]); continue; }
            UUID userId;
            try { userId = UUID.fromString(parts[2]); }
            catch ( IllegalArgumentException e ) { log.warn("Invalid userId: \"{}\", skipping", parts[2]); continue; }
            long ts;
            try { ts = Long.parseLong(parts[3]); }
            catch ( NumberFormatException e ) { log.warn("Invalid timestamp: \"{}\", skipping", parts[3]); continue; }
            Object cacheObject = parts[4];
            cacheMap.put(key, new CacheDTO(id, userId, ts, cacheObject));
          } else {
            log.warn("Invalid format in disk storage file line: \"{}\", skipping", line);
          }
        }
      } else {
        log.info("Disk storage file not exists, returns empty map");
      }
    } catch (IOException e) {
      log.error("Disk storage loading caused IOException", e);
    }
    return cacheMap;
  }
}