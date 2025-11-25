#include <stdio.h>
#include <stdlib.h>

int main() {
    void *addr = (void *)0x405018;
    void *u = malloc(0x40);
    void *a1 = malloc(0x80);
    void *a2 = malloc(0x80);
    void *b = malloc(0x10);

    free(a1);
    *(long *)addr = 0x0007000000000000LL; 
    free(a2);
}