package cache;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheSet {
    private final CacheBlock[] blocks;
    private final LRUPolicy<Integer, Integer> lruPolicy;
    private final ReentrantReadWriteLock lock;

    public CacheSet(int k) {
        this.blocks = new CacheBlock[k];
        for (int i = 0; i < k; i++) {
            this.blocks[i] = new CacheBlock();
        }
        this.lruPolicy = new LRUPolicy<>(k);
        this.lock = new ReentrantReadWriteLock();
    }

    public Integer get(int key) {
        lock.readLock().lock();
        try {
            Integer value = lruPolicy.get(key);
            if (value != null) {
                return value;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void put(int key, int data) {
        lock.writeLock().lock();
        try {
            if (lruPolicy.containsKey(key)) {
                // Update existing entry
                lruPolicy.put(key, data);
                updateBlock(key, data);
            } else {
                // Find an empty block or evict LRU
                CacheBlock targetBlock = findEmptyOrLRUBlock();
                targetBlock.setKey(key);
                targetBlock.setData(data);
                targetBlock.setValid(true);
                lruPolicy.put(key, data);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private CacheBlock findEmptyOrLRUBlock() {
        for (CacheBlock block : blocks) {
            if (!block.isValid()) {
                return block;
            }
        }
        // If no empty block, evict LRU
        int lruKey = lruPolicy.getEldestKey();
        for (CacheBlock block : blocks) {
            if (block.getKey() == lruKey) {
                return block;
            }
        }
        // This should never happen if the sizes are consistent
        throw new IllegalStateException("Inconsistent cache state");
    }

    private void updateBlock(int key, int data) {
        for (CacheBlock block : blocks) {
            if (block.isValid() && block.getKey() == key) {
                block.setData(data);
                return;
            }
        }
    }
}