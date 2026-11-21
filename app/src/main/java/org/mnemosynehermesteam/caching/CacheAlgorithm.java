package org.mnemosynehermesteam.caching;

public enum CacheAlgorithm {
  LRU,
  LFU,
  TLRU;

  public <V> Cache<V> create(int capacity) {
    return switch (this) {
      case LRU -> new LRUCache<>(capacity);
      case LFU -> new LFUCache<>(capacity);
      case TLRU -> new TLRUCache<>(capacity);
    };
  }
}
