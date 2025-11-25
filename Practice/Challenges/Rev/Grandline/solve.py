def isValid(row, col, maze, rows, cols):
    return 0 <= row < rows and 0 <= col < cols and maze[row][col] == 0

def dfs(row, col, maze, n, res, path, rows, cols):
    if row == 2 and col == 15:
        res.append(path)
        return

    movement = "WSAD"
    directions = [(-1, 0), (1, 0), (0, -1), (0, 1)]

    maze[row][col] = 1

    for i in range(4):
        dy, dx = directions[i]
        nextRow = row + dy
        nextCol = col + dx
        if isValid(nextRow, nextCol, maze, rows, cols):
            path += movement[i]
            dfs(nextRow, nextCol, maze, n, res, path, rows, cols)
            path = path[:-1]
        
    maze[row][col] = 0

maze = [
    " X   XXXXX   XXX",
    " X X     X X  XX",
    "   XXXXX   XX   "
]

grid = []

for i in maze:
    m = []
    for j in i:
        if j == "X":
            m.append(1)
        else:
            m.append(0)
    grid.append(m)

path = ""
n = len(grid)
res = []

dfs(0, 0, grid, n, res, path, len(grid), len(grid[0]))

if not res:
    print("No path discovered")
else:
    print(f'path: {" ".join(res)}')


