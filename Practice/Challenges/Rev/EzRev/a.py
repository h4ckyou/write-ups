from z3 import *

x, y, z, t = Int('x'), Int('y'), Int('z'), Int('t')

s = Solver()
s.add(x + y + z + t == 35)

if s.check() == sat:
    m = s.model()
    print(f"x = {m[x]}")
    print(f"y = {m[y]}")
    print(f"z = {m[z]}")
    print(f"t = {m[t]}")

