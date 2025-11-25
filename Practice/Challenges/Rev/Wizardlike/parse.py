MAP_SIZE = 100

def parse_map(file_path):

    with open(file_path, "rb") as f:
        data = f.read()

    if len(data) < MAP_SIZE * MAP_SIZE:
        raise ValueError(f"Map file too small: {len(data)} bytes, expected {MAP_SIZE*MAP_SIZE}")

    grid = []
    for row in range(MAP_SIZE):
        start = row * MAP_SIZE
        end = start + MAP_SIZE
        grid.append(list(data[start:end]))

    return grid

def print_map(grid):
    for row in grid:
        print("".join(chr(c) if isinstance(c, int) else c for c in row))


if __name__ == "__main__":
    import sys
    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} <map_file>")
        sys.exit(1)

    grid = parse_map(sys.argv[1])
    print_map(grid)
