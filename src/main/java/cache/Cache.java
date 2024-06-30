package cache;

import java.util.concurrent.atomic.AtomicInteger;

public class Cache{
    private final CacheSet[] sets;
    private final int setCount;
    private final int k;
    private final CacheMetrics metrics;
    private final AtomicInteger mainMemory;

    public Cache(int cacheSize, int blockSize, int k){
        this.setCount = cacheSize / (blockSize * k);
        this.k = k;
        this.sets = new CacheSet[setCount];
        for(int i = 0; i< setCount; i++){
            this.sets[i] = new CacheSet(k);
        }
        this.metrics = new CacheMetrics();
        this.mainMemory = new AtomicInteger(0);
    }

    private int getSetIndex(int key){
        return Math.abs(key % setCount);
    }

    public Integer getData(int key){
        int setIndex = getSetIndex(key);
        Integer data = sets[setIndex].get(key);
        if(data != null){
            metrics.incrementHits();
            return data;
        }
        else{
            metrics.incrementMisses();
            data = mainMemory.incrementAndGet();
            putData(key, data);
            return data;
        }
    }
    public void putData(int key, int data){
        int setIndex = getSetIndex(key);
        sets[setIndex].put(key, data);
    }

    public CacheMetrics getMetrics(){
        return metrics;
    }

    public int getK(){
        return k;
    }
}