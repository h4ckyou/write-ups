/*
 *  gcc chal.c -o chal -no-pie
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main(void) {
  unsigned char *addr;
  unsigned long long int ord;

  alarm(60);
  setbuf(stdout, NULL); // puts/prin

  printf("It's time to see if you can exploit this only with one bit again!\nEnter the address you want to flip:");

  scanf("%p", &addr);
  printf("Which bit do you want to flip?: ");
  scanf("%llu", &ord);
  if (ord >= 8) {
    puts("Idiot");
    exit(0);
  }
 
  *addr |= 1 << ord; 
  exit(0);
}
