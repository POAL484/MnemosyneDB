package org.mnemosynehermesteam.caching;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
public abstract class Cache<V> implements Cacheable<String, V> {

  private final int capacity;
  private final Map<String, V> cache = new HashMap<>();
  private final MinHeap heap = new MinHeap();
  private long timer = 0;

  protected Cache(int capacity) {
    if (capacity <= 0) {
      log.error("Invalid configuration: capacity must be more than 0");
      throw new IllegalArgumentException("capacity должна быть > 0");
    }
    this.capacity = capacity;
  }

  // какой count получает ключ при вставке
  protected abstract long initialCount();

  // какой count получает ключ при обращении к нему
  protected abstract long nextCount(long oldCount);

  @Override
  public synchronized Optional<V> get(String query) {
    V answer = cache.get(query);
    if (answer != null) {
      log.debug("inMemory cache {} found", query);
      touch(query);
    }
    return Optional.ofNullable(answer);
  }

  @Override
  public synchronized void put(String query, V answer) {
    if (cache.containsKey(query)) {
      cache.put(query, answer);
      touch(query);
      log.debug("cache already has key {}, updated to {}", query, answer);
      return;
    }
    if (cache.size() >= capacity) {
      MinHeap.Node deletedNode = deleteMoreUnuseful();
      log.debug("inMemory capacity overflows, deleted cache with key: {}", deletedNode.key());
    }
    cache.put(query, answer);
    heap.insert(query, initialCount(), timer++);
    log.debug("inserted new cache {} with value {}", query, answer);
  }

  @Override
  public synchronized boolean delete(String query) {
    heap.remove(query);
    return cache.remove(query) != null;
  }

  @Override
  public synchronized int size() {
    return cache.size();
  }

  private MinHeap.Node deleteMoreUnuseful() {
    MinHeap.Node min = heap.extractMin();
    if (min != null) {
      cache.remove(min.key());
    }
    return min;
  }

  private void touch(String query) {
    MinHeap.Node node = heap.get(query);
    heap.update(query, nextCount(node.count()), timer++);
  }
}