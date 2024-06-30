#include "cache.h"
#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>

Cache* initCache(int cacheSize, int blockSize, int numWays) {
    Cache* cache = (Cache*)malloc(sizeof(Cache));
    cache->cacheSize = cacheSize;
    cache->blockSize = blockSize;
    cache->numWays = numWays;
    cache->numSets = cacheSize / (blockSize * numWays);
    cache->sets = (CacheSet*)malloc(cache->numSets * sizeof(CacheSet));
    pthread_mutex_init(&cache->cacheMutex, NULL);
    cache->hits = 0;
    cache->misses = 0;

    for (int i = 0; i < cache->numSets; i++) {
        cache->sets[i].blocks = (CacheBlock*)malloc(numWays * sizeof(CacheBlock));
        cache->sets[i].lruQueue = (int*)malloc(numWays * sizeof(int));
        cache->sets[i].lruIndex = 0;
        for (int j = 0; j < numWays; j++) {
            cache->sets[i].blocks[j].valid = 0;
            cache->sets[i].lruQueue[j] = j;
        }
    }

    return cache;
}

int* getData(Cache* cache, int* key) {
    pthread_mutex_lock(&cache->cacheMutex);
    int setIndex = (*key) % cache->numSets;

    for (int i = 0; i < cache->numWays; i++) {
        if (cache->sets[setIndex].blocks[i].valid && cache->sets[setIndex].blocks[i].key == *key) {
            updateLRU(cache, setIndex, i);
            cache->hits++;
            pthread_mutex_unlock(&cache->cacheMutex);
            return &cache->sets[setIndex].blocks[i].data;
        }
    }
    cache->misses++;
    pthread_mutex_unlock(&cache->cacheMutex);
    return NULL;
}

void putData(Cache* cache, int* key, int data) {
    pthread_mutex_lock(&cache->cacheMutex);
    int setIndex = (*key) % cache->numSets;

    for (int i = 0; i < cache->numWays; i++) {
        if (!cache->sets[setIndex].blocks[i].valid) {
            cache->sets[setIndex].blocks[i].key = *key;
            cache->sets[setIndex].blocks[i].data = data;
            cache->sets[setIndex].blocks[i].valid = 1;
            updateLRU(cache, setIndex, i);
            pthread_mutex_unlock(&cache->cacheMutex);
            return;
        }
    }

    int lruBlockIndex = cache->sets[setIndex].lruQueue[cache->sets[setIndex].lruIndex];
    cache->sets[setIndex].blocks[lruBlockIndex].key = *key;
    cache->sets[setIndex].blocks[lruBlockIndex].data = data;
    updateLRU(cache, setIndex, lruBlockIndex);
    pthread_mutex_unlock(&cache->cacheMutex);
}

void updateLRU(Cache* cache, int setIndex, int blockIndex) {
    int* lruQueue = cache->sets[setIndex].lruQueue;
    int i;
    for (i = 0; i < cache->numWays; i++) {
        if (lruQueue[i] == blockIndex) {
            break;
        }
    }
    for (; i > 0; i--) {
        lruQueue[i] = lruQueue[i - 1];
    }
    lruQueue[0] = blockIndex;
}

void printMetrics(Cache* cache) {
    printf("Hits: %d, Misses: %d\n", cache->hits, cache->misses);
}

double calculateAMAT(Cache* cache) {
    double hitTime = 10;  // Cache access time in ns
    double missPenalty = 1000;  // Main memory access time in ns
    double hitRate = (double)cache->hits / (cache->hits + cache->misses);
    return hitTime + (1 - hitRate) * missPenalty;
}

void freeCache(Cache* cache) {
    for (int i = 0; i < cache->numSets; i++) {
        free(cache->sets[i].blocks);
        free(cache->sets[i].lruQueue);
    }
    free(cache->sets);
    pthread_mutex_destroy(&cache->cacheMutex);
    free(cache);
}
