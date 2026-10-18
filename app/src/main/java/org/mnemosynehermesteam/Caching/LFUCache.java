package org.mnemosynehermesteam.Caching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LFUCache {

  private final int capacity;
  private final Map<String, String> cache = new HashMap<>();
  private final MinHeap heap = new MinHeap();
  private long timer = 0;

  public LFUCache(int capacity) {
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
    heap.insert(query, 1, timer++);
  }

  public synchronized void deleteMoreUnuseful() {
    MinHeap.Node min = heap.extractMin();
    if (min != null) {
      cache.remove(min.key());
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
    MinHeap.Node node = heap.get(query);
    heap.update(query, node.count() + 1, timer++);
  }
}
