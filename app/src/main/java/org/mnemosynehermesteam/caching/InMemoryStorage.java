package org.mnemosynehermesteam.caching;

import org.mnemosynehermesteam.core.CacheDTO;

import java.util.Optional;
import java.time.Duration;

public class InMemoryStorage {

  private static final int DEFAULT_CAPACITY = 100;

  private final Cacheable<String, CacheDTO> cacheable;

  public InMemoryStorage(Cacheable<String, CacheDTO> cacheable) {
    this.cacheable = cacheable;
  }

  public InMemoryStorage(CacheAlgorithm algorithm) {
    this(algorithm, DEFAULT_CAPACITY);
  }

  public InMemoryStorage(CacheAlgorithm algorithm, int capacity) {
    this(algorithm.create(capacity));
  }

  public InMemoryStorage() {
    this(CacheAlgorithm.LRU);
  }


  public void save(String key, CacheDTO value) {
    cacheable.put(key, value);
  }

  public void save(String key, CacheDTO value, Duration ttl) { cacheable.put(key, value, ttl); }

  public Optional<CacheDTO> get(String key) {
    return cacheable.get(key);
  }

  public boolean delete(String key) {
    return cacheable.delete(key);
  }
}