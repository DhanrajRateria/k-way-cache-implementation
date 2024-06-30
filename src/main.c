#include "cache.h"
#include <stdio.h>

int main() {
    Cache* cache = initCache(1024, 64, 4);  // Example parameters: cache size, block size, num ways

    int key1 = 1, key2 = 2, key3 = 3;
    putData(cache, &key1, 100);
    putData(cache, &key2, 200);

    int* data = getData(cache, &key1);
    if (data != NULL) {
        printf("Data for key1: %d\n", *data);
    } else {
        printf("Data for key1 not found\n");
    }

    printMetrics(cache);
    printf("AMAT: %.2f ns\n", calculateAMAT(cache));

    freeCache(cache);
    return 0;
}
