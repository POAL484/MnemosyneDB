package org.mnemosynehermesteam.caching;

import org.mnemosynehermesteam.core.CacheDTO;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j 
public class InMemoryStorage {

  private static final int DEFAULT_CAPACITY = 100;

  private final Cacheable<String, CacheDTO> cacheable;

  public InMemoryStorage(Cacheable<String, CacheDTO> cacheable) {
    this.cacheable = cacheable;
  }

  public InMemoryStorage(CacheAlgorithm algorithm, int capacity) {
    this(algorithm.create(capacity));
    log.info("Using {} caching algorithm with {} capacity", algorithm, capacity);
  }

  public InMemoryStorage() {
    this(CacheAlgorithm.LRU, DEFAULT_CAPACITY);
  }




  public void save(String key, CacheDTO value) {
    cacheable.put(key, value);
  }

  public Optional<CacheDTO> get(String key) {
    return cacheable.get(key);
  }

  public boolean delete(String key) {
    return cacheable.delete(key);
  }
}