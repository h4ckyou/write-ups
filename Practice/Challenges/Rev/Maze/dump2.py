import os
import sys
from time import sleep
path = sys.argv[0]
current_directory = os.getcwd()
index_file = 'maze.png'

# if '.py' in path:
#     print("Ignoring the problem won't make it disappear;")
#     print('confronting and addressing it is the true path to resolution.')
#     sys.exit(0)

if not os.path.exists(os.path.join(current_directory, index_file)):
    print("Ok that's good but I guess that u should now return from the previous path")
    sys.exit(0)

index = open(index_file, 'rb').read()
seed = index[4817] + index[2624] + index[2640] + index[2720]

print('\n\nG00d!! you could escape the obfuscated path')
print('take this it may help you: ')
sleep(2)
print(f'''\nseed({seed})\nfor i in range(300):\n    randint(32,125)\n''')
print('Be Careful!!!! the route from here is not safe.')

sys.exit(0)