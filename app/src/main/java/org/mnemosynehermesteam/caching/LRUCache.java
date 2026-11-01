package org.mnemosynehermesteam.caching;

public class LRUCache<V> extends Cache<V> {

  private static final long NO_COUNT = 0;

  public LRUCache(int capacity) {
    super(capacity);
  }

  // count у всех одинаковый, поэтому куча сравнивает только время:
  // вытесняется тот, к кому дольше всех не обращались
  @Override
  protected long initialCount() {
    return NO_COUNT;
  }

  @Override
  protected long nextCount(long oldCount) {
    return NO_COUNT;
  }
}