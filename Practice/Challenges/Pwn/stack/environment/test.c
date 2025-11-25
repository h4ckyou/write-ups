#include <stdio.h>

void hehe(void){
    puts("called");
}

int main(void){
    puts("hi");
    hehe();
    return 0; // --> on the stack
}