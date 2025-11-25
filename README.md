# write-ups

Various write-ups from various CTFs..

Mostly will contain pwn and reverse engineering.

## Write-ups Index

<details>
  <summary><strong>ROP Challenges</strong></summary>

  - **0CTF TCTF 2022** --> babyheap
    * [write-up](https://github.com/)
    > *seccomp in place, heap overflow due to type confusion,  do chunk overlap for leak, then two tcache poisonning attacks*<br>
    > *code execution via forging dtor_list table in tls-storage, and erasing the random value at fs:0x30*<br>

  - **DiceCTF HOPE 2022** --> catastrophe
    * [write-up](https://github.com/)
    > *double free in fastbin, then overwrite libc strlen got entry with system() address*<br>
    > *code execution when calling puts() function (that calls strlen...)*<br>

  - **BSides.Algiers.2023** --> just pwnme
    * [solve script](https://github.com/)
    > *double free in fastbin, then get allocation on environ, leak environ, get allocation on stack, write ROP on stack*<br>

</details>
