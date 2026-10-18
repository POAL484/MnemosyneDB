package org.mnemosynehermesteam.Caching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LRUCache {

  private static final long NO_COUNT = 0;

  private final int capacity;
  private final Map<String, String> cache = new HashMap<>();
  private final MinHeap heap = new MinHeap();
  private long timer = 0;

  public LRUCache(int capacity) {
    if (capacity <= 0) throw new IllegalArgumentException("capacity должна быть > 0");
    this.capacity = capacity;
  }

  public synchronized String get(String query) {
    String answer = cache.get(query);
    if (answer != null) {
      touch(query);
    }
    return answer;
  }


  public synchronized void add(String query, String answer) {
    if (cache.containsKey(query)) {
      cache.put(query, answer);
      touch(query);
      return;
    }
    if (cache.size() >= capacity) {
      deleteMoreUnuseful();
    }
    cache.put(query, answer);
    heap.insert(query, NO_COUNT, timer++);
  }


  public synchronized void deleteMoreUnuseful() {
    MinHeap.Node oldest = heap.extractMin();
    if (oldest != null) {
      cache.remove(oldest.key());
    }
  }

  public synchronized boolean remove(String query) {
    heap.remove(query);
    return cache.remove(query) != null;
  }

  public synchronized int size() {
    return cache.size();
  }

  private void touch(String query) {
    heap.update(query, NO_COUNT, timer++);
  }
}
