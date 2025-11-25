#include <stdio.h>

int calculate(int year) {
    int month = 12; 
    int day = 25;

    int v3 = year - 2;
    day += v3;

    int hmm = (year / -100 + year / 4 + 23 * month / 9 + day + 4 + year / 400) % 7;
    
    return hmm;
}

int main() {
    int year;
    
    for (year = 0xffffff; year <= 0xffffffffff; year++) {
        int hmm = calculate(year);
        
        if (hmm == 0) {
            printf("Year that makes computation 0 on December 25: %d\n", year);
            break; 
        }
    }

    return 0;
}

/*
16777216
12
25
0
0
0
*/

