package cache;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;

public class CacheTest {
    
    @Test
    public void testCacheOperations() {
        Cache cache = new Cache(1024, 4, 4);

        cache.putData(1, 100);
        assertEquals(100, cache.getData(1));

        for (int i = 2; i <= 1000; i++) {
            cache.putData(i, i * 100);
        }

        assertNotEquals(100, cache.getData(1));

        CacheMetrics metrics = cache.getMetrics();
        assertTrue(metrics.getHitRate() > 0);
        assertTrue(metrics.getMissRate() > 0);
        assertTrue(metrics.getAMAT() > 0);
    }

    @Test
    public void testConcurrency() throws InterruptedException {
        Cache cache = new Cache(1024, 4, 4);
        int numThreads = 10;
        int numOperationsPerThread = 10000;

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < numOperationsPerThread; j++) {
                    int key = (int) (Math.random() * 1000);
                    cache.getData(key);
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        CacheMetrics metrics = cache.getMetrics();
        assertEquals(numThreads * numOperationsPerThread, metrics.getHits() + metrics.getMisses());
    }
}
