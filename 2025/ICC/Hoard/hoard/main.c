#include <stdlib.h>
#include <string.h>
#include <unistd.h>

void reads(const char *prompt, char *buf, size_t len) {
  write(1, prompt, strlen(prompt));
  for (size_t i = 0; i < len; i++) {
    if (read(0, buf + i, 1) != 1)
      _exit(1);
    if (buf[i] == '\n') {
      buf[i] = '\0';
      break;
    }
  }
}

unsigned readi(const char *prompt) {
  char buf[0x10] = { 0 };
  reads(prompt, buf, sizeof(buf)-1);
  return (unsigned)atoi(buf);
}

char *note;

int main() {
  for (size_t i = 0; i < 0x100; i++) {
    switch (readi("> ")) {
      case 1: reads("data: ", note = malloc(8), 8); break;
      case 2: write(1, note, strlen(note)); write(1, "\n", 1); break;
      case 3: free(note); break;
      default: _exit(0);
    }
  }
  _exit(0);
}
