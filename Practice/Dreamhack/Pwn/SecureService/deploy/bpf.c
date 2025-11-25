// Name: secbpf_alist.c 
// Compile: gcc -o secbpf_alist secbpf_alist.c 
# include  <fcntl.h> 
# include  <linux/audit.h> 
# include  <linux/filter.h> 
# include  <linux/seccomp.h> 
# include  <linux/unistd.h> 
# include  <stddef.h> 
# include  <stdio.h> 
# include  <stdlib.h> 
# include  <sys/mman.h> 
# include  <sys/prctl.h> 
# include  <unistd.h> 
#include <string.h>

# define  ALLOW_SYSCALL(name)                               \ 
  BPF_JUMP(BPF_JMP + BPF_JEQ + BPF_K, __NR_##name, 0, 1), \ 
      BPF_STMT(BPF_RET + BPF_K, SECCOMP_RET_ALLOW) 
# define  KILL_PROCESS BPF_STMT(BPF_RET + BPF_K, SECCOMP_RET_KILL) 
# define  syscall_nr (offsetof(struct seccomp_data, nr)) 
# define  arch_nr (offsetof(struct seccomp_data, arch)) 
/* architecture x86_64 */ 
# define  ARCH_NR AUDIT_ARCH_X86_64 


struct  sock_filter  filter[] = { 
    ALLOW_SYSCALL (open), 
    ALLOW_SYSCALL (read), 
    ALLOW_SYSCALL (execve), 
    KILL_PROCESS, 
}; 

struct  sock_fprog  prog = { 
    .len = ( unsigned  short )( sizeof (filter) /  sizeof (filter[ 0 ])), 
    .filter = filter, 
}; 


int   sandbox  ()   { 

  if  ( prctl (PR_SET_NO_NEW_PRIVS,  1 ,  0 ,  0 ,  0 ) ==  -1 ) { 
    return  -1 ; 
  } 

  if  ( prctl (PR_SET_SECCOMP, SECCOMP_MODE_FILTER, &prog) ==  -1 ) { 
    perror ( "Seccomp filter error\n" ); 
    return  -1 ; 
  } 

  return  0 ; 
} 


void  banned ()  { fork(); } 
int  main ( int  argc,  char * argv[])  { 
  char  buf[ 256 ]; 
  int  fd; 
  memset (buf,  0 ,  sizeof (buf)); 
  sandbox (); 
  if  (argc <  2 ) { 
    banned (); 
  } 
  fd =  open ( "/bin/sh" , O_RDONLY); 
  read (fd, buf,  sizeof (buf) -  1 ); 
  write ( 1 , buf,  sizeof (buf)); 
  return  0 ; 
} 