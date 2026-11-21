package org.mnemosynehermesteam.caching;

import org.mnemosynehermesteam.core.CacheDTO;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CacheEvictionTest {

    // Проверяем, что LRU вытесняет List, если к Map обращались позже
    @Test
    void shouldEvictListFromLRUCache() {
        LRUCache<Object> cache = new LRUCache<>(2);

        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("list", list);

        cache.get("map");

        cache.put("set", set);

        assertTrue(cache.get("list").isEmpty());
        assertEquals(map, cache.get("map").orElseThrow());
        assertEquals(set, cache.get("set").orElseThrow());
        assertEquals(2, cache.size());
    }

    // Проверяем, что LRU вытесняет Map, если List использовали позже
    @Test
    void shouldEvictMapFromLRUCache(){
        LRUCache<Object> cache = new LRUCache<>(2);

        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("list", list);
        cache.get("list");
        cache.put("set", set);

        assertTrue(cache.get("map").isEmpty());
        assertEquals(list, cache.get("list").orElseThrow());
        assertEquals(set, cache.get("set").orElseThrow());
        assertEquals(2, cache.size());
    }

    // Проверяем, что LRU вытесняет Set, если Map использовали позже
    @Test
    void shouldEvictSetFromLRUCache(){
        LRUCache<Object> cache = new LRUCache<>(2);

        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("set", set);
        cache.get("map");
        cache.put("list", list);

        assertTrue(cache.get("set").isEmpty());
        assertEquals(list, cache.get("list").orElseThrow());
        assertEquals(map, cache.get("map").orElseThrow());
        assertEquals(2, cache.size());
    }

    // Проверяем, что LFU вытесняет List, к которому обращались реже
    @Test
    void shouldEvictListFromLFUCache(){
        LFUCache<Object> cache = new LFUCache<>(2);
        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("list", list);

        for (int i = 0; i < 10; i++){
            cache.get("map");
        }
        cache.get("list");
        cache.get("list");

        cache.put("set", set);

        assertTrue(cache.get("list").isEmpty());
        assertEquals(map, cache.get("map").orElseThrow());
        assertEquals(set, cache.get("set").orElseThrow());
        assertEquals(2, cache.size());
    }

    // Проверяем, что LFU вытесняет Map, к которому обращались реже
    @Test
    void shouldEvictMapFromLFUCache(){
        LFUCache<Object> cache = new LFUCache<>(2);
        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("list", list);

        for (int i = 0; i < 10; i++){
            cache.get("list");
        }
        cache.get("map");
        cache.get("map");

        cache.put("set", set);

        assertTrue(cache.get("map").isEmpty());
        assertEquals(list, cache.get("list").orElseThrow());
        assertEquals(set, cache.get("set").orElseThrow());
        assertEquals(2, cache.size());
    }

    // Проверяем, что LFU вытесняет Set, к которому обращались реже
    @Test
    void shouldEvictSetFromLFUCache(){
        LFUCache<Object> cache = new LFUCache<>(2);
        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map);
        cache.put("set", set);

        for (int i = 0; i < 10; i++){
            cache.get("map");
        }
        cache.get("set");
        cache.get("set");

        cache.put("list", list);

        assertTrue(cache.get("set").isEmpty());
        assertEquals(map, cache.get("map").orElseThrow());
        assertEquals(list, cache.get("list").orElseThrow());
        assertEquals(2, cache.size());
    }

    // Если частота обращений одинаковая, удаляем самый давно использованный объект
    @Test
    void shouldEvictLeastRecentlyUsedWhenFrequencyIsEqual() {
        LFUCache<Object> cache = new LFUCache<>(2);

        cache.put("map", Map.of("a", 1));
        cache.put("list", List.of(1, 2, 3));

        cache.get("map");
        cache.get("list");

        cache.put("set", Set.of(4, 5, 6));

        assertTrue(cache.get("map").isEmpty());
        assertTrue(cache.get("list").isPresent());
        assertTrue(cache.get("set").isPresent());
    }

    // Проверяем вытеснение CacheDTO с List через InMemoryStorage
    @Test
    void shouldEvictCacheDTOWithList() {
        InMemoryStorage storage = new InMemoryStorage(CacheAlgorithm.LRU, 2);

        UUID userId = UUID.randomUUID();

        CacheDTO mapDTO = new CacheDTO(
                UUID.randomUUID(),
                userId,
                System.currentTimeMillis(),
                Map.of("a", 1)
        );

        CacheDTO listDTO = new CacheDTO(
                UUID.randomUUID(),
                userId,
                System.currentTimeMillis(),
                List.of(1, 2, 3)
        );

        CacheDTO setDTO = new CacheDTO(
                UUID.randomUUID(),
                userId,
                System.currentTimeMillis(),
                Set.of(4, 5, 6)
        );

        storage.save("map", mapDTO);
        storage.save("list", listDTO);

        storage.get("map");

        storage.save("set", setDTO);

        assertTrue(storage.get("list").isEmpty());

        assertEquals(mapDTO, storage.get("map").orElseThrow());
        assertEquals(setDTO, storage.get("set").orElseThrow());
    }

    // Проверяем, что TLRU удаляет просроченный Map при добавлении Set
    @Test
    void shouldRemoveExpiredObject() throws InterruptedException {
        TLRUCache<Object> cache = new TLRUCache<>(2);

        Map<String, Integer> map = Map.of("a", 1);
        List<Integer> list = List.of(1, 2, 3);
        Set<Integer> set = Set.of(4, 5, 6);

        cache.put("map", map, Duration.ofMillis(100));
        cache.put("list", list, Duration.ofSeconds(10));

        Thread.sleep(200);

        cache.put("set", set);

        assertTrue(cache.get("map").isEmpty());
        assertEquals(list, cache.get("list").orElseThrow());
        assertEquals(set, cache.get("set").orElseThrow());
        assertEquals(2, cache.size());
    }

    // Проверяем, что size() удаляет просроченные объекты перед подсчётом
    @Test
    void shouldRemoveExpiredObjectsWhenCheckingSize()
            throws InterruptedException {

        TLRUCache<Object> cache = new TLRUCache<>(2);

        cache.put("map", Map.of("a", 1), Duration.ofMillis(100));
        cache.put("list", List.of(1, 2, 3), Duration.ofMillis(100));

        assertEquals(2, cache.size());

        Thread.sleep(200);

        assertEquals(0, cache.size());
        assertTrue(cache.get("map").isEmpty());
        assertTrue(cache.get("list").isEmpty());
    }

    // Проверяем, что повторный put обновляет значение и TTL
    @Test
    void shouldUpdateTTLWhenPuttingExistingKey()
            throws InterruptedException {

        TLRUCache<Object> cache = new TLRUCache<>(2);

        Map<String, Integer> oldMap = Map.of("a", 1);
        Map<String, Integer> newMap = Map.of("b", 2);

        cache.put("map", oldMap, Duration.ofMillis(500));

        Thread.sleep(100);

        cache.put("map", newMap, Duration.ofSeconds(10));

        Thread.sleep(500);

        assertEquals(newMap, cache.get("map").orElseThrow());
        assertEquals(1, cache.size());
    }

    // Если TTL ни у кого не истёк, TLRU вытесняет объект по обычному LRU
    @Test
    void shouldEvictLRUWhenNoObjectsExpired() {
        TLRUCache<Object> cache = new TLRUCache<>(2);

        cache.put("map", Map.of("a", 1), Duration.ofMinutes(10));
        cache.put("list", List.of(1, 2, 3), Duration.ofMinutes(10));

        cache.get("map");

        cache.put("set", Set.of(4, 5, 6), Duration.ofMinutes(10));

        assertTrue(cache.get("list").isEmpty());
        assertTrue(cache.get("map").isPresent());
        assertTrue(cache.get("set").isPresent());
        assertEquals(2, cache.size());
    }

    // Проверяем, что TTL работает при сохранении CacheDTO через InMemoryStorage
    @Test
    void shouldExpireCacheDTOThroughInMemoryStorage()
            throws InterruptedException {

        InMemoryStorage storage =
                new InMemoryStorage(CacheAlgorithm.TLRU, 2);

        UUID userId = UUID.randomUUID();

        CacheDTO mapDTO = new CacheDTO(
                UUID.randomUUID(),
                userId,
                System.currentTimeMillis(),
                Map.of("a", 1)
        );

        storage.save(
                "map",
                mapDTO,
                Duration.ofMillis(100)
        );

        assertEquals(mapDTO, storage.get("map").orElseThrow());

        Thread.sleep(200);

        assertTrue(storage.get("map").isEmpty());
    }
}
