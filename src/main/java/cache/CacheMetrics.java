package main.java.cache;

public class CacheMetrics {
    private long hits;
    private long misses;
    private long totalAccesses;

    public void incrementHits() {
        hits++;
        totalAccesses++;
    }

    public void incrementMisses() {
        misses++;
        totalAccesses++;
    }

    public double getHitRate() {
        return totalAccesses == 0 ? 0 : (double) hits / totalAccesses;
    }

    public double getMissRate() {
        return totalAccesses == 0 ? 0 : (double) misses / totalAccesses;
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
                hits, misses, getHitRate() * 100, getMissRate() * 100, getAMAT() * 1e9);
    }
}