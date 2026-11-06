package org.mnemosynehermesteam.diskstorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mnemosynehermesteam.core.CacheDTO;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DiskStorage {
  Path filePath = Path.of("userdata.jsonl");
  static ObjectMapper mapper = new ObjectMapper();
  public record CommonObject (
      String  key,
      UUID id,
      UUID userId,
      long ts,
      String typeOfCacheObject,
      Object cacheObject
  ){}
  static CommonObject createCommonObject(String key, CacheDTO cacheDTO, String typeOfCacheObject) {
    return new CommonObject(key, cacheDTO.id(), cacheDTO.userId(), cacheDTO.ts(), typeOfCacheObject, cacheDTO.cacheObject());
  }
  static CommonObject CommonObjWithCheckingTypes(String key, CacheDTO cacheDTO) {
    if (cacheDTO.cacheObject() == null) {
      return createCommonObject(key, cacheDTO, "Null");
    }
    else if (cacheDTO.cacheObject() instanceof Integer) {
      return createCommonObject(key, cacheDTO, "Integer");
    }
    else if (cacheDTO.cacheObject() instanceof Long) {
      return createCommonObject(key, cacheDTO, "Long");
    }
    else if (cacheDTO.cacheObject() instanceof String) {
      return createCommonObject(key, cacheDTO, "String");
    }
    else if (cacheDTO.cacheObject() instanceof Boolean) {
      return createCommonObject(key, cacheDTO, "Boolean");
    }
    else if (cacheDTO.cacheObject() instanceof Double) {
      return createCommonObject(key, cacheDTO, "Double");
    }
    else if (cacheDTO.cacheObject() instanceof Map<?,?>) {
      return createCommonObject(key, cacheDTO, "Map");
    }
    else if (cacheDTO.cacheObject() instanceof List<?>) {
      return createCommonObject(key, cacheDTO, "List");
    }
    else if (cacheDTO.cacheObject() instanceof Set<?>) {
      return createCommonObject(key, cacheDTO, "Set");
    }
    throw new IllegalArgumentException();
  }
  public void save(String key, CacheDTO cacheDTO) {
    try {
      CommonObject commonObject = CommonObjWithCheckingTypes(key, cacheDTO);
      String json = mapper.writeValueAsString(commonObject);
      Files.writeString(filePath, json + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static CacheDTO createValueObj(CommonObject commonObject) {
    Object value;
    if (commonObject.typeOfCacheObject().equals("Null")) {
      return new CacheDTO(commonObject.id, commonObject.userId, commonObject.ts, null);
    }
    else if (commonObject.typeOfCacheObject().equals("Integer")) {
      value = mapper.convertValue(commonObject.cacheObject, Integer.class);
    }
    else if (commonObject.typeOfCacheObject().equals("Long")) {
      value = mapper.convertValue(commonObject.cacheObject, Long.class);
    }
    else if (commonObject.typeOfCacheObject().equals("String")) {
      value = mapper.convertValue(commonObject.cacheObject, String.class);
    }
    else if (commonObject.typeOfCacheObject().equals("Boolean")) {
      value = mapper.convertValue(commonObject.cacheObject, Boolean.class);
    }
    else if (commonObject.typeOfCacheObject().equals("Double")) {
      value = mapper.convertValue(commonObject.cacheObject, Double.class);
    }
    else if (commonObject.typeOfCacheObject().equals("Map")) {
      value = mapper.convertValue(commonObject.cacheObject, Map.class);
    }
    else if (commonObject.typeOfCacheObject().equals("List")) {
      value = mapper.convertValue(commonObject.cacheObject, List.class);
    }
    else if (commonObject.typeOfCacheObject().equals("Set")) {
      value = mapper.convertValue(commonObject.cacheObject, Set.class);
    }
    else {
      throw new IllegalArgumentException();
    }
    return new CacheDTO(commonObject.id, commonObject.userId, commonObject.ts, value);
  }
  public Map<String, CacheDTO> loadAllCacheDTO() {
    Map<String, CacheDTO> cacheMap = new HashMap<>();
    try {
      if (Files.exists(filePath)) {
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
          CommonObject commonObject = mapper.readValue(line, CommonObject.class);
          CacheDTO value = createValueObj(commonObject);
          cacheMap.put(commonObject.key, value);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cacheMap;
  }
}