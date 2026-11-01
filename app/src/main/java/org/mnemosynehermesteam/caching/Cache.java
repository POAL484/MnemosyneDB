package org.mnemosynehermesteam.caching;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Cache<V> implements Cacheable<String, V> {

  private final int capacity;
  private final Map<String, V> cache = new HashMap<>();
  private final MinHeap heap = new MinHeap();
  private long timer = 0;

  protected Cache(int capacity) {
    if (capacity <= 0) throw new IllegalArgumentException("capacity должна быть > 0");
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
      touch(query);
    }
    return Optional.ofNullable(answer);
  }

  @Override
  public synchronized void put(String query, V answer) {
    if (cache.containsKey(query)) {
      cache.put(query, answer);
      touch(query);
      return;
    }
    if (cache.size() >= capacity) {
      deleteMoreUnuseful();
    }
    cache.put(query, answer);
    heap.insert(query, initialCount(), timer++);
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

  private void deleteMoreUnuseful() {
    MinHeap.Node min = heap.extractMin();
    if (min != null) {
      cache.remove(min.key());
    }
  }

  private void touch(String query) {
    MinHeap.Node node = heap.get(query);
    heap.update(query, nextCount(node.count()), timer++);
  }
}