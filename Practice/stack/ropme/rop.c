#include <stdio.h>

void greet_me(){
    char name[0x64];
    
    puts("Prove your worth hackerman!");
    gets(name);

}

int main(int argc, char *argv[]){
    greet_me();

    return 0;
}