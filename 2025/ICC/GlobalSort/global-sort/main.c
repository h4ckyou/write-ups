#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <locale.h>

void read_str(char *buf, int size)
{
	char c;
	for(int i = 0; i < size; i++){
		c = getchar();
		if(c == '\n') {
			break;
		}
		buf[i] = c;
	}
	return;
}

int compare_strings(const void *a, const void *b) {
	return strcoll(*(const char **)a, *(const char **)b);
}

void do_sort()
{
	unsigned char buffer[80];
	unsigned char* pair[2] = {0};
	memset(buffer, 0, sizeof(buffer));
	pair[0] = getenv("FLAG");
	if(pair[0] == NULL) {
		puts("getenv error!");
		exit(1);
	}
	puts("Input: ");
	read_str(buffer, 96);
	if(buffer[0] < 'a' || buffer[0] > 'z') {
		puts("Your input should start with lower case!!\n");
		exit(1);
	}
	pair[1] = buffer;

	qsort(pair, 2, sizeof(char *), compare_strings);
	write(1, pair[1], strlen(pair[1]));
	return;
}

void main()
{
	setlocale(LC_COLLATE, "C");
	do_sort();
}

__attribute__((constructor))
void init()
{
	setbuf(stdin, NULL);
	setbuf(stdout, NULL);
	setbuf(stderr, NULL);
}
