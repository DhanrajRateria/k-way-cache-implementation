#include "cache.h"
#include <stdio.h>
#include <pthread.h>

#define NUM_THREADS 10

void* threadFunc(void* arg) {
    Cache* cache = (Cache*)arg;
    int key = rand() % 100;
    putData(cache, &key, rand());
    getData(cache, &key);
    return NULL;
}

int main() {
    Cache* cache = initCache(1024, 64, 4);  // Example parameters: cache size, block size, num ways

    pthread_t threads[NUM_THREADS];
    for (int i = 0; i < NUM_THREADS; i++) {
        pthread_create(&threads[i], NULL, threadFunc, (void*)cache);
    }

    for (int i = 0; i < NUM_THREADS; i++) {
        pthread_join(threads[i], NULL);
    }

    printMetrics(cache);
    printf("AMAT: %.2f ns\n", calculateAMAT(cache));

    freeCache(cache);
    return 0;
}
