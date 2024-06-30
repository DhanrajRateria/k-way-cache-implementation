#ifndef CACHE_H
#define CACHE_H

#include <pthread.h>

typedef struct {
    int key;
    int data;
    int valid;
} CacheBlock;

typedef struct {
    CacheBlock* blocks;
    int* lruQueue;
    int lruIndex;
} CacheSet;

typedef struct {
    CacheSet* sets;
    int cacheSize;
    int blockSize;
    int numWays;
    int numSets;
    pthread_mutex_t cacheMutex;
    int hits;
    int misses;
} Cache;

Cache* initCache(int cacheSize, int blockSize, int numWays);
int* getData(Cache* cache, int* key);
void putData(Cache* cache, int* key, int data);
void updateLRU(Cache* cache, int setIndex, int blockIndex);
void printMetrics(Cache* cache);
double calculateAMAT(Cache* cache);
void freeCache(Cache* cache);

#endif
