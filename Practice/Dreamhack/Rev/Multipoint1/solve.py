from pwn import *

exe = ELF("./multipoint")

MOD = 251

def modinv(a):
    a %= MOD
    if a == 0:
        raise ZeroDivisionError("no inverse for 0")
    return pow(a, MOD-2, MOD)
    
def build_matrix(size_list, arrayone, m_final, n_buf):
    M = [[0]*n_buf for _ in range(m_final)]
    for w in size_list:
        b0 = w & 0xFF
        b1 = (w >> 8) & 0xFF
        b2 = (w >> 16) & 0xFF
        b3 = (w >> 24) & 0xFF
        if b0 >= m_final or b1 >= n_buf:
            continue
        coef = (arrayone[b2] * (b3 % MOD)) % MOD
        M[b0][b1] = (M[b0][b1] + coef) % MOD
    return M

def invert_matrix_square(A):
    """Return inverse of square matrix A mod MOD or raise if singular."""
    n = len(A)
    # make augmented [A | I]
    aug = [ [A[i][j] % MOD for j in range(n)] + [1 if i==j else 0 for j in range(n)] for i in range(n) ]
    # gaussian elimination
    r = 0
    for c in range(n):
        # find pivot
        piv = None
        for i in range(r, n):
            if aug[i][c] % MOD != 0:
                piv = i; break
        if piv is None:
            continue
        aug[r], aug[piv] = aug[piv], aug[r]
        invp = modinv(aug[r][c])
        aug[r] = [(val * invp) % MOD for val in aug[r]]
        for i in range(n):
            if i == r: continue
            factor = aug[i][c]
            if factor:
                aug[i] = [ (aug[i][k] - factor*aug[r][k]) % MOD for k in range(2*n) ]
        r += 1
    if r != n:
        raise ValueError("matrix is singular (rank < n), cannot invert")
    # extract right half as inverse
    inv = [ row[n:] for row in aug ]
    return inv

def mat_vec_mul(A, v):
    return [ sum((A[i][j]*v[j]) % MOD for j in range(len(v))) % MOD for i in range(len(A)) ]




data = exe.read(0x201020, 0x1acfc)
points = exe.read(0x21BD20, 0x40)
result = exe.read(0x021BD60, 38)
var = {}

size_list = [u32(data[i:i+4]) for i in range(0, len(data), 4)]
points = [i for i in points]
final = [i for i in result]
init_final = [0]*len(final)
MAT = 38

M = build_matrix(size_list, points, MAT, MAT)
RHS = [ (final[i] - init_final[i]) % MOD for i in range(MAT) ]
Minv = invert_matrix_square(M)
x = mat_vec_mul(Minv, RHS)
print(bytes(x))