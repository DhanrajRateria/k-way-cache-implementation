package cache;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheSet {
    private final CacheBlock[] blocks;
    private final LRUPolicy<Integer, Integer> lruPolicy;
    private final ReadWriteLock lock;

    public CacheSet(int k){
        this.blocks = new CacheBlock[k];
        for (int i = 0; i < k; i++) {
            this.blocks[i] = new CacheBlock();
        }
        this.lruPolicy = new LRUPolicy<>(k);
        this.lock = new ReentrantReadWriteLock();
    }

    public Integer get(int key) {
        lock.readLock().lock();
        try{
            if(lruPolicy.containsKey(key)){
                for(CacheBlock block : blocks){
                    if(block.isValid() && block.getKey()==key){
                        lruPolicy.get(key);
                        return block.getData();
                    }
                }
            }
            return null;
        } finally{
            lock.readLock().unlock();
        }
    }
    public void put(int key, int data){
        lock.writeLock().lock();
        try{
            if(lruPolicy.containsKey(key)){
                for(CacheBlock block : blocks){
                    if(block.isValid() && block.getKey() == key){
                        block.setData(data);
                        lruPolicy.put(key, data);
                        return;
                    }
                }
            }
            else{
                CacheBlock targetBlock = null;
                for(CacheBlock block : blocks){
                    if(!block.isValid()){
                        targetBlock = block;
                        break;
                    }
                }
                if(targetBlock == null){
                    int lruKey = lruPolicy.entrySet().iterator().next().getKey();
                    for(CacheBlock block : blocks){
                        if(block.getKey()==lruKey){
                            targetBlock = block;
                            break;
                        }
                    }
                }
                targetBlock.setKey(key);
                targetBlock.setData(data);
                targetBlock.setValid(true);
                lruPolicy.put(key, data);
            }
        }
        finally{
            lock.writeLock().unlock();
        }
    }
}
