package org.mnemosynehermesteam.caching;

public class LFUCache<V> extends Cache<V> {

  public LFUCache(int capacity) {
    super(capacity);
  }

  // каждое обращение увеличивает count: вытесняется самый редко используемый,
  // при равенстве — самый давний
  @Override
  protected long initialCount() {
    return 1;
  }

  @Override
  protected long nextCount(long oldCount) {
    return oldCount + 1;
  }
}