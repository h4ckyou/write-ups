#include <stdio.h>
#include <stdint.h>

// Function to compute modular exponentiation (base^exp % mod)
uint64_t modular_pow(uint64_t base, uint64_t exp, uint64_t mod) {
    uint64_t result = 1;
    base = base % mod;
    while (exp > 0) {
        if (exp % 2 == 1)  
            result = (result * base) % mod;
        exp = exp >> 1;
        base = (base * base) % mod; 
    }
    return result;
}

void find_exponent(uint64_t base, uint64_t result, uint64_t mod) {
    for (long int exp = 0; exp < mod; exp++){
        if (modular_pow(base, exp, mod) == result){
            printf("The exponent is: %llu\n", exp);
            break;
        }
    }
}

int main() {
    uint64_t base = 0x400000019;
    uint64_t result = 0x73A63A673;
    uint64_t mod = 0xffffffad5;

    find_exponent(base, result, mod);

    return 0;
}
