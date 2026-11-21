package org.mnemosynehermesteam.caching;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class TLRUCache<V> extends LRUCache<V> {

    private final Map<String, Instant> expirationTimes = new HashMap<>();

    public TLRUCache(int capacity) {
        super(capacity);
    }

    @Override
    public synchronized int size() {
        removeExpired();
        return super.size();
    }

    @Override
    public synchronized void put(String key, V value) {
        putInternal(key, value, null);
    }

    @Override
    public synchronized void put(String key, V value, Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("TTL must be positive");
        }

        putInternal(key, value, Instant.now().plus(ttl));
    }

    private void putInternal(String key, V value, Instant expirationTime) {
        removeExpired();

        super.put(key, value);

        if (expirationTime == null) {
            expirationTimes.remove(key);
        } else {
            expirationTimes.put(key, expirationTime);
        }
    }

    @Override
    public synchronized Optional<V> get(String key) {
        Instant expirationTime = expirationTimes.get(key);

        if (expirationTime != null &&
                !Instant.now().isBefore(expirationTime)) {
            delete(key);
            return Optional.empty();
        }

        return super.get(key);
    }

    private void removeExpired() {
        Instant now = Instant.now();

        Iterator<Map.Entry<String, Instant>> iterator =
                expirationTimes.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Instant> entry = iterator.next();

            if (!now.isBefore(entry.getValue())) {
                super.delete(entry.getKey());
                iterator.remove();
            }
        }
    }

    @Override
    public synchronized boolean delete(String key) {
        boolean removed = super.delete(key);
        expirationTimes.remove(key);
        return removed;
    }

    @Override
    protected void onEvict(String key) {
        expirationTimes.remove(key);
    }
}
