#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<assert.h>
#include<fcntl.h>
#include<seccomp.h>
#include<sys/prctl.h>
#include<sys/stat.h>
#include<sys/mman.h>
#include<unistd.h>
int main(int argc, char* argv[])
{// compile : gcc -o judge judge.c -lseccomp
	size_t len = strlen(argv[1]);
	assert(argc == 2); // Shellcode as argument.
	assert(len % 2 == 0); // Check if the given shellcode is valid.
	setvbuf(stdin, NULL, _IONBF, 0);
	setvbuf(stdout, NULL, _IONBF, 0);
	void* addr = (void*)mmap(0, 0x1000, 7, 0x22, -1, 0); // Area to execute shellcode.
	char* shellcode_str = argv[1]; // Shellcode string.
	unsigned char* pos = (unsigned char*)addr; // Convert string into shellcode bytes.
	for (size_t i = 0; i < len; i += 2)
	{// Convert shellcode string one byte at time.
		char byte[5] = { 0 };
		byte[0] = shellcode_str[i];
		byte[1] = shellcode_str[i + 1];
		unsigned char x = strtol(byte, NULL, 16);
		*pos = x;
		pos++;
	}
	/* Setup the SECCOMP. */
	scmp_filter_ctx ctx = seccomp_init(SCMP_ACT_KILL);
	seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(read), 0);
	seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(write), 0);
	seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(open), 0);
	seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(exit), 0);
	seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(exit_group), 0);
	seccomp_load(ctx);
	(*(void(*)())addr)(); // Execute the shellcode.
	return 0;
}