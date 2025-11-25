#include <stdio.h>
#include <sys/time.h>
#include <stdint.h>
#include <stdlib.h> 

#define diff 8

int generate(void) {
    struct timeval tv;

    gettimeofday(&tv, 0);
    uint32_t seed = (tv.tv_sec) ^ (tv.tv_usec);
    srandom(seed);
    // printf("rand: %d\n", random()); 
    return random();
}


// gcc -shared -fPIC segment.c -o segment