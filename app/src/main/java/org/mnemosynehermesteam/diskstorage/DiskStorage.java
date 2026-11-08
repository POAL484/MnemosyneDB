package org.mnemosynehermesteam.diskstorage;

import org.xerial.snappy.Snappy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mnemosynehermesteam.core.CacheDTO;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DiskStorage {

  static String compress(String json) throws IOException {
    byte[] bytes = json.getBytes();
    return Base64.getEncoder().encodeToString(Snappy.compress(bytes));
  }

  static String decompress(String encoded) throws IOException {
    byte[] bytes = Base64.getDecoder().decode(encoded);
    return new String(Snappy.uncompress(bytes));
  }

  Path filePath = Path.of("userdata.jsonl");

  static ObjectMapper mapper = new ObjectMapper();

  static CommonObject createCommonObject(String key, CacheDTO cacheDTO, String typeOfCacheObject) {
    return new CommonObject(key, cacheDTO.id(), cacheDTO.userId(), cacheDTO.ts(), typeOfCacheObject, cacheDTO.cacheObject());
  }


  static CommonObject CommonObjWithCheckingTypes(String key, CacheDTO cacheDTO) {
    if (cacheDTO.cacheObject() instanceof Long) {
      return createCommonObject(key, cacheDTO, "Long");
    }
    else if (cacheDTO.cacheObject() instanceof Set<?>) {
      return createCommonObject(key, cacheDTO, "Set");
    }
    return createCommonObject(key, cacheDTO, "AnotherType");
  }




  public void save(String key, CacheDTO cacheDTO) {
    try {
      CommonObject commonObject = CommonObjWithCheckingTypes(key, cacheDTO);
      String json = mapper.writeValueAsString(commonObject);
      String compressedjson = compress(json);
      Files.writeString(filePath, compressedjson + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static CacheDTO createValueObj(CommonObject commonObject) {
    Object value;
    if (commonObject.typeOfCacheObject().equals("Long")) {
      value = mapper.convertValue(commonObject.cacheObject(), Long.class);
    }
    else if (commonObject.typeOfCacheObject().equals("Set")) {
      value = mapper.convertValue(commonObject.cacheObject(), Set.class);
    }
    else {
      value = commonObject.cacheObject();
    }
    return new CacheDTO(commonObject.id(), commonObject.userId(), commonObject.ts(), value);
  }
  public Map<String, CacheDTO> loadCacheDTO() {
    Map<String, CacheDTO> cacheMap = new HashMap<>();
    try {
      if (Files.exists(filePath)) {
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
          String json = decompress(line);
          CommonObject commonObject = mapper.readValue(json, CommonObject.class);
          CacheDTO value = createValueObj(commonObject);
          cacheMap.put(commonObject.key(), value);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cacheMap;
  }
}