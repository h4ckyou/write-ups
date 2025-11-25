#include <stdio.h>
#include <stdlib.h>

int main() {
    char *a = malloc(0x30);
    char *b = malloc(0x500);
    char *c = malloc(0x10);

    free(b);
}