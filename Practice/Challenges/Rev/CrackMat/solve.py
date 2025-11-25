import math

def solve_quadratic(a, b, c):
    discriminant = b**2 - 4*a*c

    if discriminant > 0:
        # Two real roots
        root1 = (-b + math.sqrt(discriminant)) / (2*a)
        root2 = (-b - math.sqrt(discriminant)) / (2*a)
        return root1, root2
    elif discriminant == 0:
        # One real root (repeated)
        root = -b / (2*a)
        return root, root
    else:
        # Complex roots
        real_part = -b / (2*a)
        imaginary_part = math.sqrt(abs(discriminant)) / (2*a)
        root1 = complex(real_part, imaginary_part)
        root2 = complex(real_part, -imaginary_part)
        return root1, root2

equations = [
  [1, -204, 10404],
  [1, -216, 11664],
  [1, -194, 9409],
  [1, -206, 10609],
  [1, -246, 15129],
  [1, -200, 10000],
  [1, -102, 2601],
  [1, -232, 13456],
  [1, -202, 10201],
  [1, -228, 12996],
  [1, -218, 11881],
  [1, -210, 11025],
  [1, -220, 12100],
  [1, -194, 9409],
  [1, -220, 12100],
  [1 , -232, 13456],
  [1, -202, 10201],
  [1, -190, 9025],
  [1, -96, 2304],
  [1, -250, 15625]
]

flag = ""

for quad in equations:
    A, B, C = quad[0], quad[1], quad[2]
    root1, root2 = solve_quadratic(A, B, C)
    flag += chr(int(root1))

print(flag)