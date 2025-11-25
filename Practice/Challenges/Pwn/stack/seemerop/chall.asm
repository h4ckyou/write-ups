bits 64

%define STDIN 0
%define STDOUT 1

global _start
section .text

clear_registers:
    xor rdi, rdi
    xor rsi, rsi
    xor rdx, rdx
    xor rax, rax
    ret

super_program:
    push rbp
    mov rbp, rsp
    sub rsp, 0x20

    mov rax, 1
    mov rdi, STDOUT
    mov rsi, prompt
    mov rdx, len
    syscall

    mov rax, 0
    mov rdi, STDIN
    mov rsi, rsp
    mov rdx, 0x1000
    syscall

    leave
    ret

_start:
    call clear_registers
    call super_program
    mov rax, 0x3c
    xor rdi, rdi
    syscall
    ret

section .data
    prompt: db "Alright, I was a bit cocky last time ! I removed the help this time :)", 0xa
    len: equ $ - prompt
