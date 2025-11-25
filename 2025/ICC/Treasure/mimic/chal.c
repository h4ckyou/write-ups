/*
 *  gcc chal.c -o chal -fno-stack-protector
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int cnt;
char MIMIC_NAME[] = "Name: Mimic1";
char MIMIC_HP[] = "HP: 144";
char MIMIC_MP[] = "MP: 255";

void read_line(char *buf) {
  for (int i=0; i<32; i++) {
    int c = getchar();
    if (c == EOF || c == '\0' || c == '\n') {
      buf[i] = '\0';
      return;
    }

    buf[i] = (char)c;
  }
}

int main(void) {
  setbuf(stdout, NULL);

  char status[28]; 
  memset(status, '\xff', sizeof(status));

  puts("Mimic: If you can mimic me, I will give you the flag!");

  while (1) {
    int give_more_chances = 0;

    read_line(status);

    cnt = 0;
    cnt += strcmp(status, MIMIC_NAME) == 0;
    cnt += strcmp(status+sizeof(MIMIC_NAME), MIMIC_HP) == 0;
    cnt += strcmp(status+sizeof(MIMIC_NAME)+sizeof(MIMIC_HP), MIMIC_MP) == 0;

    if (cnt == 3) {
      puts("Mimic: Well done! You perfectly mimicked me :)");
      system("cat ./flag*");
      return 0;
    }

    if (cnt >= 2) {
      give_more_chances = 1;
    }

    if (!give_more_chances) break;

    puts("Mimic: Almost! I give you another chance.");
  }

  puts("Mimic: You failed.");
}
