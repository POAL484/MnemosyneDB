package org.mnemosynehermesteam.caching;

import java.util.Optional;
import java.time.Duration;

public interface Cacheable<K, V> {

  void put(K key, V value);

  Optional<V> get(K key);

  boolean delete(K key);

  int size();

  default void put(K key, V value, Duration ttl) {
    throw new UnsupportedOperationException(
            "TTL is not supported by this cache"
    );
  }
}