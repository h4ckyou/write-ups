#include <stdio.h>

int main() {
    for (int i = 0; i<0x100; i++) {
        printf("%d: %d\n", (char) i, i);
    }
}