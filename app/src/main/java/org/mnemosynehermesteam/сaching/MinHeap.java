package org.mnemosynehermesteam.сaching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinHeap {

  public record Node(long count, long time, String key) {}

  private final List<Node> heap = new ArrayList<>();
  private final Map<String, Integer> pos = new HashMap<>();

  private boolean less(int i, int j) {
    Node a = heap.get(i), b = heap.get(j);
    if (a.count() != b.count()) return a.count() < b.count();
    return a.time() < b.time();
  }

  private void swap(int i, int j) {
    Node a = heap.get(i), b = heap.get(j);
    heap.set(i, b);
    heap.set(j, a);
    pos.put(b.key(), i);
    pos.put(a.key(), j);
  }

  private void siftUp(int i) {
    while (i > 0) {
      int parent = (i - 1) / 2;
      if (!less(i, parent)) break;
      swap(i, parent);
      i = parent;
    }
  }

  private void siftDown(int i) {
    int n = heap.size();
    while (true) {
      int l = 2 * i + 1, r = l + 1, min = i;
      if (l < n && less(l, min)) min = l;
      if (r < n && less(r, min)) min = r;
      if (min == i) break;
      swap(i, min);
      i = min;
    }
  }

  public boolean contains(String key) {
    return pos.containsKey(key);
  }

  public Node get(String key) {
    Integer i = pos.get(key);
    return i == null ? null : heap.get(i);
  }

  public void insert(String key, long count, long time) {
    if (pos.containsKey(key)) {
      throw new IllegalArgumentException("Ключ уже в куче: " + key);
    }
    heap.add(new Node(count, time, key));
    pos.put(key, heap.size() - 1);
    siftUp(heap.size() - 1);
  }

  public void update(String key, long count, long time) {
    Integer i = pos.get(key);
    if (i == null) {
      throw new IllegalArgumentException("Нет такого ключа: " + key);
    }
    heap.set(i, new Node(count, time, key));
    siftUp(i);
    siftDown(i);
  }

  public Node getMin() {
    return heap.isEmpty() ? null : heap.get(0);
  }

  public Node extractMin() {
    if (heap.isEmpty()) return null;
    Node min = heap.get(0);
    removeAt(0);
    return min;
  }

  public boolean remove(String key) {
    Integer i = pos.get(key);
    if (i == null) return false;
    removeAt(i);
    return true;
  }

  private void removeAt(int i) {
    int last = heap.size() - 1;
    swap(i, last);
    Node removed = heap.remove(last);
    pos.remove(removed.key());
    if (i < heap.size()) {
      siftUp(i);
      siftDown(i);
    }
  }

  public int size() {
    return heap.size();
  }

  public boolean isEmpty() {
    return heap.isEmpty();
  }
}
