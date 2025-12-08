#include <stdio.h>
#include <stdlib.h>


int main() {
    char *u1 = malloc(0x18);
    char *u2 = malloc(0x80);
    char *a = malloc(0x18);
    char *b = malloc(0x80);

    for (int i = 0; i < 8; i++) {
        free(u2);
        *(long long * )(u2 + 8) = 0xdeadbeefLL;

    }

    getchar();
}