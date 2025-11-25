#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void shift(char *string, int value) {
    int length = strlen(string);
    char temp_pass[length + 1]; 

    strcpy(temp_pass, string); 

    for (int x = 0; x < length; x++) {
        string[x] = temp_pass[(x + value) % length];
    }

    printf("shift: %s\n", string);
}

void rot(char *string, int value){
	for(int x = 0; x < strlen(string); x = x + 3){
		if(string[x] >= 'a' && string[x] <= 'z'){
			string[x] = (((string[x]-'a')+13+value) % 26) + 'a';
		}
		if(string[x] >= 'A' && string[x] <= 'Z'){
			string[x] = (((string[x]-'A')+13+value) % 26) + 'A';
		}
	}

    printf("rot: %s\n", string);
}

void add(char *string, int value){
	char temp;
	int length = strlen(string);
	for(int x = 0; x < length; x++){
		if(string[x] >= '0' && string[x] <= '9'){
			string[x] = string[x] + value;
		}
	}

    printf("add: %s\n", string);
}


int main() {
    char string[] = "markuchedavid0"; 

    // shift(string, 7); 
    // rot(string, 7);
    add(string, 7);

    return 0;
}
