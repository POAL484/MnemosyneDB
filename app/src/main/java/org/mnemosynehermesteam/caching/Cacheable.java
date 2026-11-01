package org.mnemosynehermesteam.caching;

import java.util.Optional;

public interface Cacheable<K, V> {

  void put(K key, V value);

  Optional<V> get(K key);

  boolean delete(K key);

  int size();
}