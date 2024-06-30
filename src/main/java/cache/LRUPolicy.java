package cache;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.Set;

public class LRUPolicy<K, V> {
    private final ConcurrentHashMap<K, V> map;
    private final AtomicInteger size;
    private final int capacity;

    public LRUPolicy(int capacity) {
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>(capacity);
        this.size = new AtomicInteger(0);
    }

    public V get(K key) {
        V value = map.get(key);
        if (value != null) {
            map.remove(key);
            map.put(key, value);
        }
        return value;
    }

    public void put(K key, V value) {
        if (map.containsKey(key)) {
            map.remove(key);
        } else if (size.get() >= capacity) {
            K eldest = map.keySet().iterator().next();
            map.remove(eldest);
            size.decrementAndGet();
        }
        map.put(key, value);
        size.incrementAndGet();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public K getEldestKey() {
        return map.keySet().iterator().next();
    }

    public int size() {
        return size.get();
    }
}