package cache;
import java.util.concurrent.atomic.AtomicLong;

public class CacheMetrics {
    private final AtomicLong hits;
    private final AtomicLong misses;
    private final AtomicLong totalAccesses;

    public CacheMetrics() {
        this.hits = new AtomicLong(0);
        this.misses = new AtomicLong(0);
        this.totalAccesses = new AtomicLong(0);
    }

    public void incrementHits() {
        hits.incrementAndGet();
        totalAccesses.incrementAndGet();
    }

    public void incrementMisses() {
        misses.incrementAndGet();
        totalAccesses.incrementAndGet();
    }

    public long getHits() {
        return hits.get();
    }

    public long getMisses() {
        return misses.get();
    }

    public long getTotalAccesses() {
        return totalAccesses.get();
    }

    public double getHitRate() {
        long total = totalAccesses.get();
        return total == 0 ? 0 : (double) hits.get() / total;
    }

    public double getMissRate() {
        long total = totalAccesses.get();
        return total == 0 ? 0 : (double) misses.get() / total;
    }

    public double getAMAT() {
        double hitRate = getHitRate();
        double missRate = getMissRate();
        double cacheAccessTime = 10e-9; // 10 ns
        double mainMemoryAccessTime = 1e-6; // 1 μs

        return hitRate * cacheAccessTime + missRate * (cacheAccessTime + mainMemoryAccessTime);
    }

    @Override
    public String toString() {
        return String.format("Hits: %d, Misses: %d, Hit Rate: %.2f%%, Miss Rate: %.2f%%, AMAT: %.2f ns",
                hits.get(), misses.get(), getHitRate() * 100, getMissRate() * 100, getAMAT() * 1e9);
    }
}