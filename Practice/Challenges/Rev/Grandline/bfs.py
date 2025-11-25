from collections import deque

def bfs(maze, start, end):
    rows, cols = len(maze), len(maze[0])
    visited = [[False]*cols for _ in range(rows)]
    q = deque([(start, "")])  # (position, path)
    visited[start[0]][start[1]] = True

    # Directions: W=up, S=down, A=left, D=right
    moves = [(-1,0,'W'), (1,0,'S'), (0,-1,'A'), (0,1,'D')]

    while q:
        (r, c), path = q.popleft()

        # Check if we reached the goal
        if (r, c) == end:
            return path  # shortest path

        for dr, dc, m in moves:
            nr, nc = r+dr, c+dc
            if 0 <= nr < rows and 0 <= nc < cols and maze[nr][nc] == 0 and not visited[nr][nc]:
                visited[nr][nc] = True
                q.append(((nr, nc), path + m))

    return None  # no path found


maze = [
    " X   XXXXX   XXX",
    " X X     X X  XX",
    "   XXXXX   XX   "
]

grid = [[1 if c == "X" else 0 for c in row] for row in maze]
start = (0, 0)
end = (2, 15)

path = bfs(grid, start, end)

if path:
    print("Shortest path:", path)
else:
    print("No path found")
